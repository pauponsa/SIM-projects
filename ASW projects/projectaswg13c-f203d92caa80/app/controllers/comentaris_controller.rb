class ComentarisController < ApplicationController
  before_action :set_comentari, only: [:show, :edit, :update, :destroy, :upvote_comment_update, :vote_comment, :unvote_comment]
  
  ############
  #La API KEY s'ha de mirar en cada endpoint que faci falta, dintre de format.json, perquè si accedeixes des del navegador peta perquè el navegador no posa el header
  #api_key = request.headers[:HTTP_X_API_KEY]
  ############
  
  # GET /comentaris
  # GET /comentaris.json
  def index
      respond_to do |format|
        format.html {
          if(!session[:user_email].nil?)
            @user = User.find(session[:user_id])
            @comment = Comentari.all.where(:user_id => @user.id)
            @current_user_likes_comment = []
            if session[:user_email]
              @current_user_likes_comment=LikedComment.where(user_id: session[:user_id])
            end
            render :index
          end
        }
        format.json  {
          @user = User.all.where(:ltoken => request.headers["X-API-Key"]).first()
          if @user.nil?
            render json: {"status": 401,
            "error": "Unauthorized",
            "message": "You provided no api key or incorrect api key(X-API-KEY Header)"}, status: :unauthorized
            return
          end
          @comment = Comentari.all.where(:user_id => @user.id)
          comments = []
          @comment.each do |c|
            @upvotes = LikedComment.all.where(:comentari_id => c.id)
            upvote_ids = []
            @upvotes.each do |u|
              upvote_ids.push(u.user_id)
            end
              
            comments.push({
              "id": c.id,
              "text": c.text,
              "respondsToId": c.respondsToId,
              "postID": c.postID,
              "created_at": c.created_at,
              "likes": c.likes,
              "user_id": c.user_id,
              "upvote_ids": upvote_ids
            })
          end
          render json: comments.to_json
        }
      end
  end


  # GET /comentaris/1
  # GET /comentaris/1.json
  def show
    @comentari = Comentari.find(params[:id])
    @submit = Submit.find(@comentari.postID)
    @current_user_likes_comment = []
    if session[:user_email]
      @current_user_likes_comment=LikedComment.where(user_id: session[:user_id])
    end
  end

  # GET /comentaris/new
  def new
    @comentari = Comentari.new
  end

  # GET /comentaris/1/edit
  def edit
  end

  # POST /comentaris
  # POST /comentaris.json
  def create
    if(comentari_params[:text].blank?)
    else
      respond_to do |format|
        format.html { 
          @comentari = Comentari.new(comentari_params)
          @comentari.likes|=1
          if @comentari.save
            redirect_to @comentari, notice: 'Comentari was successfully created.'
          else
            render :new
          end
        }
        format.json{
          if request.headers["X-API-Key"].blank?
            render json: {
                "status": 401,
                "message": "You provided no api key (X-API-KEY Header)"
              }, status: 401
          else 
            @user = User.all.where(:ltoken => request.headers["X-API-Key"]).first()
            if @user.nil?
              render json: {
                "status": 403,
                "message": "Your api key (X-API-KEY Header) is not valid"
              }, status: 403
              return
            end
            @comentari = Comentari.new(comentari_params)
            if @comentari.respondsToId > 0
              @comentari2 = Comentari.find(@comentari.respondsToId)
              if(@comentari2.postID != @comentari.postID)
                render json: {
                "status": 404,
                "message": "The respondsToId comment is not on the PostID post"
              }, status: 404
              return
              end
            end  
            @comentari.likes|=1
            @comentari.user_id=@user.id
            if @comentari.save
              render json: {
                "status": 200,
                "commentId": @comentari.id,
                "message": "Comment posted successfully"
              }, status: :ok
            else
              render json: {
                "status": 403,
                "message": "Couldn't save comment"
              }, status: 403
            end
          end
        }
      end
    end
  end

  # PATCH/PUT /comentaris/1
  # PATCH/PUT /comentaris/1.json
  def update
    respond_to do |format|
      if @comentari.update(comentari_params)
        format.html { redirect_to @comentari, notice: 'Comentari was successfully updated.' }
        format.json { render :show, status: :ok, location: @comentari }
      else
        format.html { render :edit }
        format.json { render json: @comentari.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /comentaris/1
  # DELETE /comentaris/1.json
  def destroy
    @comentari.destroy
    respond_to do |format|
      format.html { redirect_to comentaris_url, notice: 'Comentari was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_comentari
      @comentari = Comentari.where(id: params[:id]).first
      if !@comentari
        not_found2
      end
    end

    # Only allow a list of trusted parameters through.
    def comentari_params
      params.require(:comentari).permit(:text, :user_id, :postID, :respondsToId)
    end
  end
