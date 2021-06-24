class User < ApplicationRecord
  has_secure_password

  validates :email, uniqueness: true
  validates :login_token, allow_nil: true, uniqueness: true

  validates :password, presence: true, confirmation: true, length: { minimum: 6 }, on: :create
  validates :password_confirmation, presence: true, on: :create

  validates :role, presence: true, on: :create

  mount_uploader :image, PictureUploader
  has_many :entrada_usuarios
  has_many :favourites
  has_many :ratings

  def generate_token(column)
    begin
      self[column] = SecureRandom.urlsafe_base64
    end while User.exists?(column => self[column])
  end

  def send_password_reset
      generate_token(:password_reset_token)
      self.password_reset_send_at = Time.zone.now
      save!
      print "send_password_reset-------------------------------------------------"
      UserEmailMailer.password_reset(self).deliver

  end

  def formatted_data
  {
    :id         	=> self.id,
    :name         => self.name,
    :username     => self.username,
    :email        => self.email,
    :phone        => self.phone,
    :image        => self.image.url,
    :language     => self.language,
    :latitude     => self.latitude,
    :longitude    => self.longitude,
    :role         => self.role,
    :rating       => self.rating,
    :created_at   => self.created_at,
    :updated_at   => self.updated_at
  }
  end

  def formatted_login_data
  {
    :id         	=> self.id,
    :login_token  => self.login_token,
    :name         => self.name,
    :username     => self.username,
    :email        => self.email,
    :phone        => self.phone,
    :image        => self.image.url,
    :language     => self.language,
    :latitude     => self.latitude,
    :longitude    => self.longitude,
    :role         => self.role,
    :rating       => self.rating,
    :created_at   => self.created_at,
    :updated_at   => self.updated_at
  }
  end
end
