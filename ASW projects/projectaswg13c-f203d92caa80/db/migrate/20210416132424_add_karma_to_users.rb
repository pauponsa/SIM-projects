class AddKarmaToUsers < ActiveRecord::Migration[6.1]
  def change
    add_column :users, :karma, :int, :default => 1
  end
end
