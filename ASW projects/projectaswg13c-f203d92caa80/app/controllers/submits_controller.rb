class SubmitsController < ApplicationController
  before_action :set_submit, only: [:show, :edit, :update, :destroy, :like, :upvote_update, :vote, :unvote]
  require 'uri'
  require "net/http"
  # GET /submits
  # GET /submits.json
  def index
    @submit = Submit.all.where(:text => "").order(like: :desc)
    @current_user_likes = []
    if session[:user_email]
      @current_user_likes=LikedPost.where(user_id: session[:user_id])
    end
    respond_to do |format|
        format.html {render :index}
        format.json {
          submits = []
          @submit.each do |s|
            @upvotes = LikedPost.all.where(:submit_id => s.id)
            @comment = Comentari.all.where(:postID => s.id)
            @user = User.find(s.user_id)
            upvote_ids = []
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
  # GET /submits/1
  # GET /submits/1.json
  def show
    @submit = Submit.find(params[:id])
    @comment = Comentari.all.where(:postID => params[:id]).order(:respondsToId)
    @current_user_likes = []
    
    if session[:user_email]
      @current_user_likes=LikedPost.where(user_id: session[:user_id])
    end
    
    @current_user_likes_comment = []
    if session[:user_email]
      @current_user_likes_comment=LikedComment.where(user_id: session[:user_id])
    end
    
    respond_to do |format|
      format.html {render :show}
      
      format.json  { 
        submits = []
        @upvotes = LikedPost.all.where(:submit_id => @submit.id)
        upvote_ids = []
        @user = User.find(@submit.user_id)
        @upvotes.each do |u|
          upvote_ids.push(u.user_id)
        end
        submits.push({
          "id": @submit.id,
          "title": @submit.title,
          "url": @submit.URL,
          "text": @submit.text,
          "created_at": @submit.created_at,
          "likes": @submit.like,
          "user_id": @submit.user_id,
          "user_name": @user.name,
          "upvote_ids": upvote_ids
        })
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
            comments.push({
              "id": c.id,
              "text": c.text,
              "respondsToId": c.respondsToId,
              "postID": c.postID,
              "created_at": c.created_at,
              "likes": c.likes,
              "user_id": c.user_id,
              "upvote_ids": upvote_ids,
              "respondsTo": respondsTo
            })
        end
        render :json => {:submit => submits, :comment => comments }
      }
    end
  end

  # GET /submits/new
  def new
      @submit = Submit.new
  end

  # GET /submits/1/edit
  def edit
  end
  
  # GET /submit_urls/newest
  def newest
   @submit = Submit.all.order(created_at: :desc)
    @current_user_likes = []
    if session[:user_email]
      @current_user_likes=LikedPost.where(user_id: session[:user_id])
    end
    respond_to do |format|
        format.html {render :newest}
        format.json {
          submits = []
          @submit.each do |s|
            @upvotes = LikedPost.all.where(:submit_id => s.id)
            @comment = Comentari.all.where(:postID => s.id)
            @user = User.find(s.user_id)
            upvote_ids = []
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
  
  def ask #edit
    @submit = Submit.all.where(:URL => "").order(created_at: :desc)
    @current_user_likes = []
    if session[:user_email]
      @current_user_likes=LikedPost.where(user_id: session[:user_id])
    end
    respond_to do |format|
        format.html {render :ask}
        format.json  {
          submits = []
          @submit.each do |s|
            @comment = Comentari.all.where(:postID => s.id)
            @user = User.find(s.user_id)
            @upvotes = LikedPost.all.where(:submit_id => s.id)
            upvote_ids = []
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
              "user_id": s.user_id,
              "user_name": @user.name,
              "upvote_ids": upvote_ids,
              "number_comments": @comment.count
            })
          end
          render json: submits.to_json , status: :ok}
    end
  end 

  # POST /submits
  # POST /submits.json
  def create
    
    #if @user.ltoken == request.headers["X-API-Key"]
    
    @submit = Submit.new(submit_params)

    respond_to do |format|
      format.json { 
        @user = User.all.where(:ltoken => request.headers["X-API-Key"]).first()
        if @user.nil?
          render json: {"status": 401,
          "error": "Unauthorized",
          "message": "You provided no api key or incorrect api key(X-API-KEY Header)"}, status: :unauthorized
          return
        else 
          @submit.user_id=@user.id
          session[:user_id]=@user.id
          session[:user_karma]=@user.karma
        end
      }
       format.html {} 
    end
    
    if(@submit.URL.empty? || @submit.URL =~ /\A#{URI::regexp}\z/) 
      #donothing
    else
      respond_to do |format|
        format.html { redirect_to "/submits/new"
          return
        }
        format.json {  
          render json: {"status": 422,
          "error": "Unprocessable entity",
          "message": "You provided neither title nor url or text or wrong URL"}, status: :unprocessable_entity
          return }
      end
    end
  
    if (!@submit.URL.empty? && !url_exist?(@submit.URL))
        respond_to do |format|
        format.html { render :new }
        format.json { render json: {
          "status": 422,
          "error": "Unprocessable entity",
          "message": "You provided neither title nor url or text or wrong URL"}, status: :unprocessable_entity }
        end
      
    else
    @submit.like|=1
      if(@submit.title.empty?) 
        respond_to do |format|
        format.html { render :new }
        format.json { render json: {"status": 422,
          "error": "Unprocessable entity",
          "message": "You provided neither title nor url or text or wrong URL"}, status: :unprocessable_entity }
        end
      else 
        if(@submit.URL.empty? || Submit.exists?(:URL=> @submit.URL) == false)
          temp_text=@submit.text
          if(!@submit.URL.empty? && !@submit.text.empty?) 
            @submit.text=""
          end
      
          respond_to do |format|
            if @submit.save
              user = User.find(session[:user_id])
              user.update('karma': user.karma + 1)
              session[:user_karma] += 1
        
              if(!@submit.URL.empty? && !temp_text.empty?)
              @comentari = Comentari.new(:text => temp_text, :user_id =>@submit.user_id, :postID => @submit.id.to_i, :respondsToId => "0", :likes => 1)
              @comentari.save
              end

              format.html { redirect_to "/submit/newest", notice: 'Submit was successfully created.' }
              format.json { render @submit, status: :created}
            else
              format.html { render :new }
              format.json { render json: {"status": 422,
                "error": "Unprocessable entity",
                "message": "Failed to save"}, status: :unprocessable_entity }
            end
          end
        else
          respond_to do |format|
            @s = Submit.all.where(:URL => @submit.URL)
              format.html { redirect_to "/submits/" + @s.pluck(:id).join() , notice: 'URL already exists.' }
              format.json { render json: @s, status: :conflict }
            end
        end
      end
    end
  end
  # POST /submits/comment
  def comment
    params.permit!
    if params[:comentari][:text].blank?
    else
    @comment = Comentari.new(params[:comentari])
    respond_to do |format|
      if @comment.save
        user = User.find(session[:user_id])
        user.update('karma': user.karma + 1)
        session[:user_karma] += 1
        format.html { redirect_to "/submits/"+@comment.postID.to_s, notice: 'Comment was successfully created.' }
        format.json { render :show, status: :created, location: @comment }
      else
        format.html { render :new }
        format.json { render json: @comment.errors, status: :unprocessable_entity }
      end
    end
  end
  end
  # PATCH/PUT /submits/1
  # PATCH/PUT /submits/1.json
  def update
    respond_to do |format|
      if @submit.update(submit_params)
        format.html { redirect_to @submit, notice: 'Submit was successfully updated.' }
        format.json { render :show, status: :ok, location: @submit }
      else
        format.html { render :edit }
        format.json { render json: @submit.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /submits/1
  # DELETE /submits/1.json
  def destroy
    @submit.destroy
    respond_to do |format|
      format.html { redirect_to submits_url, notice: 'Submit was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_submit
      @submit = Submit.where(id: params[:id]).first
      if !@submit
        not_found2
        
      end
    end

    # Only allow a list of trusted parameters through.
    def submit_params
      params.require(:submit).permit(:title, :URL, :text, :user_id, :like)
    end
  end
  
  def url_exist?(url_string)
    begin
    url = URI.parse(url_string)
    req = Net::HTTP.new(url.host, url.port)
    req.use_ssl = (url.scheme == 'https')
    path = url.path if url.path.present?
    res = req.request_head(path || '/')
    if res.kind_of?(Net::HTTPRedirection)
      url_exist?(res['location']) # Go after any redirect and make sure you can access the redirected URL 
    else
      res.code[0] != "4" #false if http code starts with 4 - error on your side.
    end
    rescue
      false #false if can't find the server
    end
  end
