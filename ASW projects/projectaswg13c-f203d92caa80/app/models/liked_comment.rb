class LikedComment < ApplicationRecord
  belongs_to :user
  belongs_to :comentari
end
