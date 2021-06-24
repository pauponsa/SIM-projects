class AddAuthorToEventos < ActiveRecord::Migration[6.1]
  def change
  	add_column :eventos, :author, :string 
  end
end
