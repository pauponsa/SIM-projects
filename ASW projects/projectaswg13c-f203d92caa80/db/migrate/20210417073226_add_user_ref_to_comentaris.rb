class AddUserRefToComentaris < ActiveRecord::Migration[6.1]
  def change
    add_reference :comentaris, :users, null: false, foreign_key: true
  end
end
