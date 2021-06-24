class ChangeComentColum < ActiveRecord::Migration[6.1]
  def change
    rename_column :comentaris, :users_id, :user_id
  end
end
