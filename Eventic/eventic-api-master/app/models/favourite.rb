class Favourite < ApplicationRecord
    belongs_to :evento
    belongs_to :user 
end
