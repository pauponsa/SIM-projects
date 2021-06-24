class RatingsController < ApplicationController
  before_action :set_rating, only: [:show, :destroy]
  before_action :check_logged_customer, only: [:create]

  # GET /ratings
  # GET /ratings.json
  def index
    @ratings = Rating.all
  end

  # GET /ratings/1
  # GET /ratings/1.json
  def show
  end

  # GET /rating_user_evento
  def rating_user_evento
    @rating = Rating.find_by(:customer_id => params[:customer_id], :evento_id => params[:evento_id])
    if @rating
      render json: @rating.rating.to_json()
    else
      #si no te valoracio enviem 0
      render json: 0.0.to_json()
    end
  end

  # GET /ratings_company
  # GET /ratings_company.json
  def ratings_company
    @user = User.find_by(:id => params[:company_id])
    if @user.rating ==  nil or @user.rating == -1
      @message="La companyia no ha estat valorada"
      render json: @message
    else
      render json: @user.rating.to_json
    end
  end

  #GET /numvaloracions
  def numvaloracions
    @valoracions = Rating.where(company_id: params[:company_id])
    render json: @valoracions.count.to_json
  end

  # POST /ratings
  # POST /ratings.json
  def create
    if(@check_user)
      @valoracio = Rating.find_by(customer_id: params[:customer_id], evento_id: params[:evento_id])
      if @valoracio
        # UPDATE RATING
        if @valoracio.update(rating_params.except(:token))
          @valoracionsTotals = []
          @sum = 0
          @valoracionsTotals = Rating.where(company_id: params[:company_id])
          @valoracionsTotals.each do |rt|
            @sum = @sum + rt.rating
            @valoracio = @sum / @valoracionsTotals.count
          end
          @cmp = User.find_by(:id => params[:company_id])
          @cmp.rating = @valoracio
          @cmp.save()
          render json: @valoracio.to_json
        else
          render json: @valoracio.errors, status: :unprocessable_entity
        end
      else
        # CREATE RATING
        @rating = Rating.create(rating_params.except(:token))
        @user = User.find_by(:login_token => params[:token])
        @rating.customer_id = @user.id
        auto = false
        if rating_params[:company_id]==@user_id
          auto=true
        end
        if auto
          @message="ERROR: El client i la companyia han de ser diferents"
          render json: @message
        elsif @rating.save
          @valoracions = []
          @sum = 0
          @valoracions = Rating.where(company_id: params[:company_id])
          @valoracions.each do |rt|
            @sum = @sum + rt.rating
            @valoracio = @sum / @valoracions.count
          end
          @cmp = User.find_by(:id => params[:company_id])
          @cmp.rating = @valoracio
          @cmp.save()
          render json: @rating.to_json
        else
          render json: @rating.errors, status: :unprocessable_entity
        end
      end
    else
      @msg="ERROR: Usuari no autoritzat"
      render json: @msg, status: :unauthorized, location: @favourite
    end
  end

  # DELETE /ratings/1
  # DELETE /ratings/1.json
  def destroy
    cmp_id=@rating.company_id
    @valoracions = Rating.where(company_id: cmp_id)
    @valoracio = 0
    #si es lultima valoració es queda amb -1
    if @valoracions.count == 1
      @valoracio = -1
    else #sino es recalcula
      @sum = 0
      @valoracions.each do |rt|
        @sum = @sum + rt.rating
      end
      @valoracio = (@sum-@rating.rating) / (@valoracions.count-1)
    end
    @user = User.find_by(:id => cmp_id)
    @user.rating = @valoracio
    @user.save()
    #finalment destruim
    @rating.destroy
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_rating
      @rating = Rating.find(params[:id])
    end

    def check_logged_customer
      if (params[:token].nil? or params[:token] == "")
        @check_user=false
      else
        @user = User.find_by(:login_token => params[:token])
        if @user.nil?
          @check_user=false
        elsif @user.role == "customer" or @user.role == "google"
          @check_user=true
        else
          @check_user=false
        end
      end
    end

    # Only allow a list of trusted parameters through.
    def rating_params
      params.permit(:rating, :text, :token, :evento_id, :customer_id, :company_id)
    end
end
