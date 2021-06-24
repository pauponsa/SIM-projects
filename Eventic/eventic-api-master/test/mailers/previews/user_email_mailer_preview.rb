# Preview all emails at http://localhost:3000/rails/mailers/user_email_mailer
class UserEmailMailerPreview < ActionMailer::Preview

  # Preview this email at http://localhost:3000/rails/mailers/user_email_mailer/password_reset
  def password_reset
    UserEmailMailer.password_reset
  end

end
