class FavouritesController < ApplicationController
  #before_action :set_favourite, only: [ show update destroy ]
  before_action :check_logged_customer, only: [:create, :update, :destroy, :show]

  # GET /favourites
  # GET /favourites.json
  def index
    @favs = Favourite.all
    render json: @favs
  end

  #GET /liked/:id
  def liked
    @eventos = Array.new
    @favourites = Favourite.where(user_id: params[:user_id])
    @favourites.each do |fav|
      event = Evento.find_by(id: fav.evento_id).formatted_data.as_json()
      @eventos << event
    end
    render json: @eventos.to_json
  end

  # GET /like_event
  # GET /like_event.json
  def show
    @favourite = Favourite.all.where('user_id = ? and evento_id=?', @user.id, params[:evento_id])
    if !@favourite.blank?
      render json: "true"
    else
      render json: "false"
    end
  end

  # POST /favourites
  # POST /favourites.json
  def create
    if(@check_user)
      @favourite = Favourite.create(favourite_params.except(:token))
      @user = User.find_by(:login_token => params[:token])
      print @user.name
      @favourite.user_id = @user.id
      if @favourite.save
        render json: "S'ha afegit a fav"
      else
        render json: @favourite.errors, status: :unprocessable_entity
      end
    else
      @msg="ERROR: Usuari no autoritzat"
      render json: @msg, status: :unauthorized, location: @favourite
    end
  end

  # DELETE /favourites/1
  # DELETE /favourites/1.json
  def destroy
    @favourite = Favourite.find_by(user_id: @user.id, evento_id: params[:evento_id])
    @favourite.destroy
  end

private
    # Use callbacks to share common setup or constraints between actions.
    def set_favourite
      @favourite = Favourite.find(params[:id])
    end

    # Only allow a list of trusted parameters through.
    def favourite_params
      params.permit(:id, :token, :evento_id, :user_id)
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
end
