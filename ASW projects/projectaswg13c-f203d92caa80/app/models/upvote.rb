class Upvote < ApplicationRecord
  belongs_to :submit
  belongs_to :usuari
end
