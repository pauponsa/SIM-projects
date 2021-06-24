class Follower < ApplicationRecord
 	
 	belongs_to :user, optional: false, class_name: "User", foreign_key: "company_id"
    belongs_to :user, optional: false, class_name: "User", foreign_key: "customer_id"
end