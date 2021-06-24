class LikedPost < ApplicationRecord
  belongs_to :submit
  belongs_to :user
end
