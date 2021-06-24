class SessionsController < ApplicationController
#logging de un usuari, log out, omniauth
  def omniauth
    #obtenim l'access token del servidor de google
    auth = request.env['omniauth.auth']
    
    user =  User.find_or_create_by(uid: auth['uid'], provider: auth['provider']) do |u|
      u.name = auth['info']['name']
      u.email = auth['info']['email']
      u.password = SecureRandom.hex(16)
      string_length = 8
      u.ltoken = rand(36**string_length).to_s(36)
    end 
    if user.valid?
      session[:user_id] = user.id
      session[:user_email] = user.email
      session[:user_name] = user.name
      session[:user_karma] = user.karma
      session[:user_login_token] = user.ltoken
      
      redirect_to "/submit/newest"
    else
        flash[:message] = user.error.full_message.join(", ")
        redirect_to  "/submit/index"
    end
  end 
  def destroy
    session[:user_id] = nil
    session[:user_email] = nil
    session[:user_name] = nil
    session[:user_karma] = nil
    session[:user_login_token] = nil
    redirect_to '/submit/newest'
  end
  
end

