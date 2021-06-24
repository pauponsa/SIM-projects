class PasswordResetsController < ApplicationController

  def create
    user = User.find_by_email(params[:email])
    user.send_password_reset 
    if user
    render json: {}, status: :ok
    end
  end
  def edit
    @user = User.find_by_password_reset_token!(params[:id])
  end
  def update
    @message1=""
    @message2=""
    @user = User.find_by_password_reset_token!(params[:id])
    if @user.password_reset_send_at < 2.hours.ago
       redirect_to "https://eventic-api.herokuapp.com/edit" + "/" + @user.password_reset_token, :alert => "Password reset has expired."
    elsif @user.update(user_params)
      @user.save
      @message2=""
      @message1 = "Your password has been changed successfully"
      render :edit
    else
      @message1=""
      @message2 = "Your password change has failed"
      render :edit
    end
  end

  private

  def user_params
    params.require(:user).permit(:password, :id, :password_confirmation, :password_reset_token)
  end

end
