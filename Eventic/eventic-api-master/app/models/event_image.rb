class EventImage < ApplicationRecord

  belongs_to :evento
  mount_uploader :image, PictureUploader

end
