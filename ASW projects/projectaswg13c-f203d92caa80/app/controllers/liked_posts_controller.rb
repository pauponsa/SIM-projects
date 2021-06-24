class LikedPostsController < ApplicationController
  before_action :set_liked_post, only: [:show, :edit, :update, :destroy]

  # GET /liked_posts
  # GET /liked_posts.json
  def index
    @liked_posts = LikedPost.all
  end

  # GET /liked_posts/1
  # GET /liked_posts/1.json
  def show
  end

  # GET /liked_posts/new
  def new
    @liked_post = LikedPost.new
  end

  # GET /liked_posts/1/edit
  def edit
  end

  # POST /liked_posts
  # POST /liked_posts.json
  def create
    @liked_post = LikedPost.new(liked_post_params)

    respond_to do |format|
      if @liked_post.save
        format.html { redirect_to @liked_post, notice: 'Liked post was successfully created.' }
        format.json { render :show, status: :created, location: @liked_post }
      else
        format.html { render :new }
        format.json { render json: @liked_post.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /liked_posts/1
  # PATCH/PUT /liked_posts/1.json
  def update
    respond_to do |format|
      if @liked_post.update(liked_post_params)
        format.html { redirect_to @liked_post, notice: 'Liked post was successfully updated.' }
        format.json { render :show, status: :ok, location: @liked_post }
      else
        format.html { render :edit }
        format.json { render json: @liked_post.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /liked_posts/1
  # DELETE /liked_posts/1.json
  def destroy
    @liked_post.destroy
    respond_to do |format|
      format.html { redirect_to liked_posts_url, notice: 'Liked post was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_liked_post
      @liked_post = LikedPost.find(params[:id])
    end

    # Only allow a list of trusted parameters through.
    def liked_post_params
      params.require(:liked_post).permit(:submit_id, :user_id)
    end
end
