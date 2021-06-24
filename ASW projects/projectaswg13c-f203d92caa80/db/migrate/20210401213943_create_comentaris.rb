class CreateComentaris < ActiveRecord::Migration[6.1]
  def change
    create_table :comentaris do |t|
      t.string :text
      t.string :user
      t.integer :postID
      t.integer :respondsToId
      t.integer :likes

      t.timestamps
    end
  end
end
