class AddlogintokenToUsers < ActiveRecord::Migration[6.1]
  def change
    add_column :users, :ltoken, :string
  end
end
