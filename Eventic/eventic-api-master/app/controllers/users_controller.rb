class UsersController < ApplicationController
  before_action :set_user, only: [:show, :update, :destroy]
  before_action :check_logged_user, only: [:update, :destroy, :delete_profile_pic]


def avis_cas
  date_two_weeks_ago = (Date.today - 14.days)
  @entrada=EntradaUsuario.where(:user_id => params[:id])

  @entrada.each do |e|
    if(e.ha_participat)
      @evento=Evento.find_by_id(e.evento_id)
      if(Date.parse(@evento.start_date)>date_two_weeks_ago)
        @entrada_event=EntradaUsuario.where(:evento_id => @evento.id)
        @entrada_event.each do |u|
          if(u.ha_participat && u.user_id!=params[:id])
            @user=User.find_by_id(u.user_id)
            UserEmailMailer.aviso(@user).deliver_now
          end
        end
      end
    end
  end
end


def edit
  user= User.find_by(:email => params[:email])
  user.send_password_reset
end

def send_password_reset
  generate_token(:password_reset_token)
  self.password_reset_sent_at = TIme.zone.deliver_now
  save!
  PasswordMailer.password_mail(self).deliver_now
end

def generate_token(column)
  begin
    self[column] = SecureRandom.urlsafe_base64
  end while User.exists?(column => self[column])
end
  # GET /users
  def index
    @users = User.all
    @users_resp=Array.new
    @users.each do |u|
       @users_resp << u.formatted_data.as_json()
    end
    # render json: @users.to_json(:only =>[:id, :name, :username, :email, :phone, :image, :language, :location, :role, :created_at, :updated_at])
      render json: @users_resp
  end

  # GET /users/1
  def show
    # render json: @user.to_json(:only =>[:id, :name, :username, :email, :phone, :image, :language, :location, :role, :created_at, :updated_at])
    render json: @user.formatted_data.as_json()
  end

  # POST /users
  def create
    @user = User.new(user_params)
    if @user.role != "company" and @user.role != "customer" and @user.role != "google"
      render json: @user.errors, status: :unprocessable_entity
      return
    end

    if @user.save
      render json: @user.formatted_data.as_json(), status: :created, location: @user
    else
      render json: @user.errors, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /users/1
  def update
    if @check
      if @user.update(user_params.except(:login_token, :email, :role))
        @evento=Evento.all.where(:id_creator => @user.id)
        @evento.each do |e|
            e.author=@user.name
            e.save
        end
        render json: @user.formatted_data.as_json()
      else
        render json: @user.errors, status: :unprocessable_entity
      end
    else
      render json: @user.errors, status: :unprocessable_entity
    end
  end

  # DELETE /users/1
  def destroy
    if @check
      if @user.role == "customer" or @user.role == "google"
        #delete entrada_usuarios
        EntradaUsuario.where(:user_id => @user.id).destroy_all
        #delete valoracions
        #delete followers
        Follower.where(:customer_id => @user.id).destroy_all
        #delete favourites
        Favourite.where(:user_id => @user.id).destroy_all
        # => delete ratings
        Rating.where(:customer_id => @user.id).destroy_all
      else
        @created_events = Evento.where(:id_creator => @user.id)
        #for all created_events
        @created_events.each do |event|
          # => delete entrada_usuarios
          EntradaUsuario.where(:evento_id => event.id).destroy_all
          # => delete event_tags
          EventTag.where(:evento_id => event.id).destroy_all
          # => delete favourites
          Favourite.where(:evento_id => event.id).destroy_all
          # => delete ratings
          Rating.where(:evento_id => @evento.id).destroy_all
          # => delete event_images
          event.event_images.each do |image|
            image.destroy
          end
          # => delete created_event
          event.destroy
        end
        #delete followers
        Follower.where(:company_id => @user.id).destroy_all
        #delete valoracions
      end
      @user.destroy
    else
      render json: @user.errors, status: :unprocessable_entity
    end
  end

  #POST /login
  def login
    @user = User.find_by(:email => params[:email])
    if @user.authenticate(params[:password])
      #give_token
      token = rand(36**12).to_s(36)
      while User.find_by(:login_token => token) do
        token = rand(36**12).to_s(36)
      end
      if @user.update_attribute(:login_token, token)
        render json: @user.formatted_login_data, status: :ok, location: @user
      else
        render json: @user.errors, status: :unprocessable_entity
      end
    else
      #bad response
      render json: @user.errors, status: :unprocessable_entity
    end
  end

  #POST /logout
  def logout
    @user = User.find_by(:login_token => params[:login_token])
    if @user
      if @user.update_attribute(:login_token, nil)
        render json: @user, status: :ok, location: @user
      else
        render json: @user.errors, status: :unprocessable_entity
      end
    else
      #bad response
      render json: @user.errors, status: :unprocessable_entity
    end
  end

  #DELETE /profile_pic
  def delete_profile_pic
    if @check
      @user.image.remove!
      if @user.save
        render json: @user, status: :ok, location: @user
      else
        render json: @user.errors, status: :unprocessable_entity
      end
    else
      render json: @user.errors, status: :unprocessable_entity
    end
  end


  private
    # Use callbacks to share common setup or constraints between actions.
    def set_user
      @user = User.find(params[:id])
    end

    def check_logged_user
      if (params[:login_token].nil? or params[:login_token] == "")
        @check=0
        render json: {}, status: :unauthorized, location: @user
      else
        @user = User.find_by(:login_token => params[:login_token])
        if @user
          @check=1
        else
          @check=0
          render json: {}, status: :unauthorized, location: @user
        end
      end
    end

    # Only allow a list of trusted parameters through.
    def user_params
      params.permit(:id, :email, :name, :username, :image, :language, :location, :login_token, :password, :password_confirmation, :role, :rating, :nif, :phone)
    end
end
