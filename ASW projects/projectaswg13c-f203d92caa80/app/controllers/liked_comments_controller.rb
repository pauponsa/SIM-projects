class LikedCommentsController < ApplicationController
  before_action :set_liked_comment, only: [:show, :edit, :update, :destroy]

  # GET /liked_comments
  # GET /liked_comments.json
  def index
    @liked_comments = LikedComment.all
  end

  # GET /liked_comments/1
  # GET /liked_comments/1.json
  def show
  end

  # GET /liked_comments/new
  def new
    @liked_comment = LikedComment.new
  end

  # GET /liked_comments/1/edit
  def edit
  end

  # POST /liked_comments
  # POST /liked_comments.json
  def create
    @liked_comment = LikedComment.new(liked_comment_params)

    respond_to do |format|
      if @liked_comment.save
        format.html { redirect_to @liked_comment, notice: 'Liked comment was successfully created.' }
        format.json { render :show, status: :created, location: @liked_comment }
      else
        format.html { render :new }
        format.json { render json: @liked_comment.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /liked_comments/1
  # PATCH/PUT /liked_comments/1.json
  def update
    respond_to do |format|
      if @liked_comment.update(liked_comment_params)
        format.html { redirect_to @liked_comment, notice: 'Liked comment was successfully updated.' }
        format.json { render :show, status: :ok, location: @liked_comment }
      else
        format.html { render :edit }
        format.json { render json: @liked_comment.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /liked_comments/1
  # DELETE /liked_comments/1.json
  def destroy
    @liked_comment.destroy
    respond_to do |format|
      format.html { redirect_to liked_comments_url, notice: 'Liked comment was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_liked_comment
      @liked_comment = LikedComment.find(params[:id])
    end

    # Only allow a list of trusted parameters through.
    def liked_comment_params
      params.require(:liked_comment).permit(:user_id, :comentari_id)
    end
end
