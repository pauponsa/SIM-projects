class CreateEventTags < ActiveRecord::Migration[6.1]
  def change
    create_table :event_tags do |t|
      t.belongs_to :evento
      t.belongs_to :tag

      t.timestamps
    end
  end
end
