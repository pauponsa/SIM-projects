class CreateEntradaUsuarios < ActiveRecord::Migration[6.1]
  def change
    create_table :entrada_usuarios do |t|
      t.belongs_to :user
      t.belongs_to :evento 
      t.string :code
      

      t.timestamps
    end
  end
end
