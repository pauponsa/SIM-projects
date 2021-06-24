class FollowersController < ApplicationController
  before_action :set_follower, only:[:update, :destroy, :followed]
  before_action :check_logged_customer, only: [:create, :update, :destroy, :followed]

  #GET /follower
  #GET /follower.json
  def index
    @followships = Follower.all
    render json: @followships
  end

  #GET /followed_events
  #GET /followed_events.json
  def followed_events
    @followed = Follower.all.where(:customer_id => params[:customer_id])
    @followed_events=Array.new
    @eventos_nous=Array.new
    @followed.each do |f|
      @followed_events = Evento.all.where(:id_creator => f.company_id)
      @followed_events.each do |e|
          @eventos_nous << e.formatted_data.as_json()
      end
    end
    render json: @eventos_nous
  end

  #GET /follower/comp
  #GET /follower/comp.json
  #def comp

  #end

  # POST /follower
  # POST /follower.json
  def create
    if(@check)
      @customer = User.find_by_id(params[:customer_id])
      @company= User.find_by_id(params[:company_id])
      if(Follower.all.where('company_id = ? and customer_id =?', params[:company_id],params[:customer_id]).first.nil? && (@customer.role == "customer" or @customer.role == "google") && @company.role=="company")
        @follower = Follower.create(follower_params.except(:token))
        if @follower.save
          render json: @follower, status: :created, location: @follower
        else
          render json: @follower.errors, status: :bad_request
        end
      else
        @msg="ERROR: alguna cosa ha sortit malament"
        render json: @msg, status: :bad_request, location: @follower
      end
    else
      @msg="ERROR: Usuari no autoritzat"
      render json: @msg, status: :unauthorized, location: @follower
    end
  end

  #PUT /follower/id
  #PUT /follower/id.json
  def update
    if(@check)
      @follower.update(event_params.except(:token))
      if follower.save
        render json: @follower, status: :ok, location: @follower
      else
        render json: @follower.errors, status: :unprocessable_entity
      end
    end
  end

  # DELETE /follower
  # DELETE /follower.json
  def destroy
    if(@check)
      if @follower.destroy
        render json: {}, status: :ok, location: @follower
      else
        render json: @follower.errors, status: :unprocessable_entity
      end
    end
  end

  def followed
    if !@follower.blank?
      render json: "true"
    else
      render json: "false"
    end
  end

private

  def set_follower
    @follower = Follower.all.where('company_id = ? and customer_id =?', params[:company_id], params[:customer_id]).first
    if(!@follower.nil?)
      @follower = Follower.find(@follower.id)
    else
      render json: {}, status: :unprocessable_entity
    end
  end

  def check_logged_customer
    if (params[:token].nil? or params[:token] == "")
      @check=false
    else
      @user = User.find_by(:login_token => params[:token])
      if @user.role == "customer" or @user.role == "google"
        @check=true
      else
        @check=false
      end
    end
  end

  def follower_params
    params.permit(:company_id, :customer_id, :token)
  end
end
