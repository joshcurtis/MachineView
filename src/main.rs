extern crate gtk;
use gtk::traits::*;
use gtk::signal::Inhibit;


// Give a list of parameters, create a labeled entry for each
fn build_entry_box(params: Vec<&str>) -> (Vec<gtk::Entry>, Vec<gtk::Label>, Vec<gtk::Box>) {
    let n = params.len();
    let entries: Vec<gtk::Entry> = (0..n).map(|_| {
        gtk::Entry::new().unwrap()
    })
        .collect();
    let labels: Vec<gtk::Label> = (0..n).map(|i| {
        let name = format!("{}", params[i]);
        gtk::Label::new(&name).unwrap()})
        .collect();

    let entry_boxes: Vec<gtk::Box> = (0..n).map(|i| {
        let entry_box = gtk::Box::new(gtk::Orientation::Horizontal, 0).unwrap();
        entry_box.pack_start(&labels[i], false, false, 10);
        entry_box.pack_start(&entries[i], false, false, 0);
        entry_box
    })
        .collect();
    return (entries, labels, entry_boxes);

}

fn gui_main() {
    gtk::init().ok().expect("Unable to load GTK");

    let params = vec!["Temperature", "Operating System",];
    let n = params.len();
    let (entries, labels, entry_boxes) = build_entry_box(params);

    let button = gtk::Button::new_with_label("Generate INI file").unwrap();
    button.connect_clicked(move |_| {
        // Debugging info for now
        for entry in &entries {
            let s = entry.get_text().unwrap();
            println!("{}",s)
        }
    });


    // Create a button and associate it with a file chooser dialog
    let file_button = gtk::Button::new_with_label("Find INI file").unwrap();
    let file_window = gtk::Window::new(gtk::WindowType::Toplevel).unwrap();
    let file_chooser =  gtk::FileChooserDialog::new("Load ini file",
                                                    Some(&file_window),
                                                    gtk::FileChooserAction::Open,
                                                    [("Ok", gtk::ResponseType::Accept), ("Cancel", gtk::ResponseType::Cancel)]);
    file_button.connect_clicked(move |_| {
        file_chooser.show_all();
        if file_chooser.run() == gtk::ResponseType::Accept as i32 {
            let filename = file_chooser.get_filename().unwrap();
            println!("{}", filename);
        }
        file_chooser.hide();
    });

    // Display everything
    let display = gtk::Box::new(gtk::Orientation::Vertical,10).unwrap();
    for i in 0..n {
        display.pack_start(&entry_boxes[i], false, false, 0);
    }
    display.pack_start(&button, false, false, 0);
    display.pack_start(&file_button, false, false, 0);


    let window = gtk::Window::new(gtk::WindowType::Toplevel).unwrap();
    window.set_title("3D printer ini generator");
    window.connect_delete_event(|_, _| {
        gtk::main_quit();
        Inhibit(false)
    });
    window.set_border_width(10);
    window.set_window_position(gtk::WindowPosition::Center);
    window.set_default_size(350, 400);
    window.add(&display);

    window.show_all();
    gtk::main();
}


fn main() {
    gui_main();
}
