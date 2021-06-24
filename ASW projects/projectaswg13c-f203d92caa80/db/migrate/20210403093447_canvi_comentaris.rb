class CanviComentaris < ActiveRecord::Migration[6.1]
  def change
    change_column :comentaris, :user, :string
  end
end
