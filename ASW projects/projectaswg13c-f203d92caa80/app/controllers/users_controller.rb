class UsersController < ApplicationController
  
  before_action :set_user, only: [:show, :edit, :update, :destroy]

  # GET /users
  # GET /users.json
  def index
    @users = User.all
  end

  # GET /users/1
  # GET /users/1.json
  def show
    @user = User.where(id: params[:id]).first
    if @user.blank?
      respond_to do |format|
        format.html {raise ActionController::RoutingError.new('Not Found')}
        format.json { render json:
          {
          "status": 404,
          "error": "Not Found",
          "message": "No user with that ID"
          }, status: 404 }
      end
    else
    respond_to do |format|
        format.html {
          if params[:id] == session[:user_id].to_s
            render :edit
          else render :show
          end
        }
        format.json  {
            userJson = {
              "id":  @user.id,
              "name":  @user.name,
              "about": @user.about,
              "email":  @user.email,
              "created_at":  @user.created_at,
              "karma":  @user.karma
            }
            render json: userJson
            }
          end
    end
  end
  
  # GET /users/1/submits
  def submits
    @submit = Submit.all.where(:user_id => params[:id])
    @user = User.where(id: params[:id]).first
    @current_user_likes = []
    
    if session[:user_email]
      @current_user_likes=LikedPost.where(user_id: session[:user_id])
    end
    if @user.blank?
      respond_to do |format|
        format.html {raise ActionController::RoutingError.new('Not Found')}
        format.json { render json:
          {
          "status": 404,
          "error": "Not Found",
          "message": "No user with that ID"
          }, status: 404 }
      end
    else
    respond_to do |format|
        format.html {render :submits}
        format.json {
          submits = []
          @submit.each do |s|
            @upvotes = LikedPost.all.where(:submit_id => s.id)
            upvote_ids = []
            @comment = Comentari.all.where(:postID => s.id)
            @user = User.find(s.user_id)
            @upvotes.each do |u|
              upvote_ids.push(u.user_id)
            end
            submits.push({
              "id": s.id,
              "title": s.title,
              "url": s.URL,
              "text": s.text,
              "created_at": s.created_at,
              "likes": s.like,
              "user_name": @user.name,
              "user_id": s.user_id,
              "upvote_ids": upvote_ids,
              "number_comments": @comment.count
            })
          end
          render json: submits.to_json , status: :ok
        }
        end
    end
  end
  
  # GET /users/1/comments
  # GET /users/1/comments.json
  def comments
    @comment = Comentari.all.where(:user_id => params[:id])
    @user = User.where(id: params[:id]).first 
    if @user.blank?
      respond_to do |format|
        format.html {raise ActionController::RoutingError.new('Not Found')}
        format.json { render json:
          {
          "status": 404,
          "error": "Not Found",
          "message": "No user with that ID"
          }, status: 404 }
      end
    else
    @current_user_likes_comment = []
    if session[:user_email]
      @current_user_likes_comment=LikedComment.where(user_id: session[:user_id])
    end
    respond_to do |format|
        format.html {render :comments}
        format.json  {
          comments = []
          @comment.each do |c|
            @upvotes = LikedComment.all.where(:comentari_id => c.id)
            upvote_ids = []
            @upvotes.each do |u|
              upvote_ids.push(u.user_id)
            end
            respondsTo = ""
            if(c.respondsToId == 0)
              @respondsPost = Submit.find(c.postID)
              respondsTo = @respondsPost.title
            else
              @respondsComment = Comentari.find(c.respondsToId)
              respondsTo = @respondsComment.text
            end
            @users = User.find(c.user_id)
            comments.push({
              "id": c.id,
              "text": c.text,
              "respondsToId": c.respondsToId,
              "postID": c.postID,
              "created_at": c.created_at,
              "likes": c.likes,
              "user_id": c.user_id,
              "upvote_ids": upvote_ids,
              "user_name": @users.name,
              "respondsTo": respondsTo
            })
          end
          render json: comments.to_json 
        }
    end
    end
  end
  
  # GET /users/favourite_submissions
  def upvoted_submissions
     respond_to do |format|
      format.json { 
        @user = User.all.where(:ltoken => request.headers["X-API-Key"]).first()
        if @user.nil?
          render json: {"status": 401,
          "error": "Unauthorized",
          "message": "You provided no api key or incorrect api key(X-API-KEY Header)"}, status: :unauthorized
          return
        else
          if(@user.id.to_i!=params[:id].to_i)
            render json: {"status": 403,
            "error": "Forbidden",
            "message": "Your api key (X-API-KEY Header) is not valid"}, status: :forbidden
            return
          else
            session[:user_id] = params[:id]
          end
        end
      }
       format.html {} 
    end
    
    
    @liked_submits = LikedPost.all.where(:user_id => session[:user_id])
    submits = []
    @liked_submits.each do |s|
      submits = submits.push(Submit.find(s.submit_id))
    end
    @submits = submits
    @user = User.find(session[:user_id])
    @current_user_likes = []
    if session[:user_email]
      @current_user_likes=LikedPost.where(user_id: session[:user_id])
    end
    respond_to do |format|
        format.html {render :upvoted_submissions}
        format.json  {
          submits_aux = []
          submits.each do |s|
            @upvotes = LikedPost.all.where(:submit_id => s.id)
            upvote_ids = []
            @upvotes.each do |u|
              upvote_ids.push(u.user_id)
            end
            @comment = Comentari.all.where(:postID => s.id)
            @user = User.find(s.user_id)
            submits_aux.push({
              "id": s.id,
              "title": s.title,
              "url": s.URL,
              "text": s.text,
              "created_at": s.created_at,
              "likes": s.like,
              "user_id": s.user_id,
              "user_name": @user.name,
              "upvote_ids": upvote_ids,
              "number_comments": @comment.count
            })
          end
          render json: submits_aux.to_json , status: :ok
        }
    end

  end
  
  # GET /users/upvoted_comments
def upvoted_comments
    session[:user_id] = params[:id]
    @liked_comments = LikedComment.all.where(:user_id => session[:user_id])
    comments = []
    @liked_comments.each do |c|
      comments = comments.push(Comentari.find(c.comentari_id))
    end
    @comments = comments
    @user = User.find(session[:user_id])
    if @user.nil?
          render json: {"status": 401,
          "error": "Unauthorized",
          "message": "You provided no api key or incorrect api key(X-API-KEY Header)"}, status: :unauthorized
          return
    end
    @current_user_likes_comment = []
    if session[:user_email]
      @current_user_likes_comment=LikedComment.where(user_id: session[:user_id])
    end
      respond_to do |format|
        format.html {render :upvoted_comments}
        format.json  {
          if @user.ltoken == request.headers["X-API-Key"]
            comments = []
            @comments.each do |c|
              @upvotes = LikedComment.all.where(:comentari_id => c.id)
              @users = User.find(c.user_id)
              upvote_ids = []
              @upvotes.each do |u|
                upvote_ids.push(u.user_id)
              end
              respondsTo = ""
              if(c.respondsToId == 0)
                @respondsPost = Submit.find(c.postID)
                respondsTo = @respondsPost.title
              else
                @respondsComment = Comentari.find(c.respondsToId)
                respondsTo = @respondsComment.text
              end
              comments.push({
                "id": c.id,
                "text": c.text,
                "respondsToId": c.respondsToId,
                "postID": c.postID,
                "created_at": c.created_at,
                "likes": c.likes,
                "user_id": c.user_id,
                "user_name": @users.name,
                "upvote_ids": upvote_ids,
                "respondsTo": respondsTo
              })
            end
            render json: comments.to_json
          else
             render json: {"status": 403,
             "error": "Forbidden",
             "message": "Your api key (X-API-KEY Header) is not valid"}, status: :forbidden
             return
          end
          }
      end
    
  end

  # PATCH/PUT /users/1
  # PATCH/PUT /users/1.json
  def update
    respond_to do |format|
        format.html { 
          if @user.update(user_params)
            if user_params[:name] != nil 
              session[:user_name] = user_params[:name]
            end
          redirect_to @user, notice: 'user was successfully updated.' 
          else
            render :edit
          end
        }
        format.json {
          if request.headers["X-API-Key"].blank?
            render json: {
                "status": 401,
                "message": "You provided no api key (X-API-KEY Header)"
              }, status: 401
          elsif @user.ltoken == request.headers["X-API-Key"]
            if @user.update(user_params)
              render json: {
                "status": 200,
                "message": "User updated sucessfully"
              }, status: :ok
            else
              render json: {
                "status": 200,
                "message": "Nothing to update"
              }, status: :ok
            end
          else  
            render json: {
                "status": 403,
                "message": "Your api key (X-API-KEY Header) is not valid"
              }, status: 403
          end
        }
      end
  end

  # DELETE /users/1
  # DELETE /users/1.json
  def destroy
    @user.destroy
    respond_to do |format|
      format.html { redirect_to users_url, notice: 'user was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_user
      @user = User.where(id: params[:id]).first
      if @user.blank?
      respond_to do |format|
        format.html {raise ActionController::RoutingError.new('Not Found')}
        format.json { render json:
          {
          "status": 404,
          "error": "Not Found",
          "message": "No user with that ID"
          }, status: 404 }
      end
      end
    end

    # Only allow a list of trusted parameters through.
    def user_params
      params.require(:user).permit(:name, :email, :about)
    end
end
