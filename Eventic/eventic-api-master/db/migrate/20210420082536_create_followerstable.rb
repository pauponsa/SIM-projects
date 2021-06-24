class CreateFollowerstable < ActiveRecord::Migration[6.1]
  def change
    create_table :followers do |t|
    	
    	t.references :company
         t.references :customer
    	t.timestamps

    end
    add_foreign_key :followers, :users, column: :company_id, primary_key: :id
    add_foreign_key :followers, :users, column: :customer_id, primary_key: :id
 
     end
end
