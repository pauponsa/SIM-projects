class RemoveCreatorFromSubmits < ActiveRecord::Migration[6.1]
  def change
    remove_column :submits, :creator, :string
  end
end
