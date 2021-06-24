class CreateRatings < ActiveRecord::Migration[6.1]
  def change
    create_table :ratings do |t|
      t.decimal :rating, :precision => 4, :scale => 3, :default => 0
      t.string :text
      t.references :company
      t.references :customer
      t.references :evento
      t.timestamps
    end
    add_foreign_key :ratings, :users, column: :company_id, primary_key: :id
    add_foreign_key :ratings, :users, column: :customer_id, primary_key: :id
    add_foreign_key :ratings, :eventos, column: :evento_id, primary_key: :id
  end
end
