class RemoveUserFromComentaris < ActiveRecord::Migration[6.1]
  def change
    remove_column :comentaris, :user, :string
  end
end
