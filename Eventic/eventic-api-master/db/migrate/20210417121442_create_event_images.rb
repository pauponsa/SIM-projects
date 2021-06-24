class CreateEventImages < ActiveRecord::Migration[6.1]
  def change
    create_table :event_images do |t|
      t.references :evento, null: false, foreign_key: true
      t.string :image

      t.timestamps
    end
  end
end
