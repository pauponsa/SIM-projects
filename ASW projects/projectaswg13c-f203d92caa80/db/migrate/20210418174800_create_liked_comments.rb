class CreateLikedComments < ActiveRecord::Migration[6.1]
  def change
    create_table :liked_comments do |t|
      t.references :users, null: false, foreign_key: true
      t.references :comentari, null: false, foreign_key: true

      t.timestamps
    end
  end
end
