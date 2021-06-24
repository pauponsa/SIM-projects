class ChangeSubmitColum < ActiveRecord::Migration[6.1]
  def change
    rename_column :submits, :users_id, :user_id
  end
end
