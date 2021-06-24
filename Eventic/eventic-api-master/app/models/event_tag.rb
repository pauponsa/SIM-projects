class EventTag < ApplicationRecord
    belongs_to :evento, optional: false
    belongs_to :tag, optional: false
end
