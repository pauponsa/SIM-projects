class Tag < ApplicationRecord
    validates :tag_name, uniqueness: true

    has_many :event_tags
    has_many :evento, :through => :event_tags
end
