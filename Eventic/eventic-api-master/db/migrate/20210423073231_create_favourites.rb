class CreateFavourites < ActiveRecord::Migration[6.1]
  def change
    create_table :favourites do |t|
      t.belongs_to :user
      t.belongs_to :evento 
      t.timestamps
    end
  end
end
