package model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.repo.Popup;
import com.example.libraryapp.R;
import com.example.libraryapp.repo.BookRepo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private static final String TAG = "Out";
    List<Book> bookList;
    BookRepo bookRepo = new BookRepo();
    CoordinatorLayout layout;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    List<Book> bookListCopy;



    public BookAdapter(ArrayList<Book> bookList) {

        this.bookList=bookList;

    }
    //Required variables go here


    //Constructor goes here

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout for item of recycler view item.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycler_layout1, parent, false);
        BookViewHolder viewHolder = new BookViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // inside on bind view holder method we are
        // setting ou data to each UI component.
        Book book = bookList.get(position);

        holder.name.setText((book.getTitle()));
        holder.description.setText(book.getDescription());
        String aUrl = book.getThumbnail().replace("http", "https");
        //Image isnt loading
        if(!aUrl.isEmpty()) {
            Picasso.get()
                    .load(aUrl)
                    .fit()
                    .placeholder(R.mipmap.ic_launcher)  // preload
                    .error(R.mipmap.ic_launcher_error)        // load error
                    .into(holder.cover);
        }

        holder.imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Popup popup =  new Popup();

                View popupView = popup.popup(v,R.layout.book_info);
                TextView author = popupView.findViewById(R.id.author);
                TextView title = popupView.findViewById(R.id.title1);
                TextView price = popupView.findViewById(R.id.price);
                TextView buyLink = popupView.findViewById(R.id.buyLink);
                String authors = "";
                if(book.getAuthors()!=null)
                    for(int i =0; i<book.getAuthors().size(); i++){
                    authors+=book.getAuthors().get(i);
                }

                if(authors!=null)
                    author.setText("Authors: "+authors);
                if(book.getTitle()!=null)
                    title.setText("Title: "+book.getTitle());
                if(book.getPublisher()!=null)
                    price.setText("Publisher: "+book.getPublisher());
                if(book.getBuyLink()!=null)
                    buyLink.setText("Buy Link: "+book.getBuyLink());

            }
        });

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout = v.findViewById(R.id.layout);
                Snackbar mySnackbar;

                if(bookRepo.manageLibrary(book, mUser.getUid(), v))
                {

                    mySnackbar = Snackbar.make(v, "Successfully added to Library", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(bookRepo.remove(book, mUser.getUid())) {
                                Snackbar mySnackbar1 = Snackbar.make(v, "The books been removed!", Snackbar.LENGTH_LONG);
                                mySnackbar1.show();
                            }

                        }
                    });
                }
                else{
                    mySnackbar = Snackbar.make(v, "Sorry that didn't work!", Snackbar.LENGTH_LONG);
                }
                mySnackbar.show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return bookList.size();
    }


    //ViewHolder class
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView name, description, author, buyLink, price, title;
        ImageButton imageButton, imageButton1;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.libname);
            description = itemView.findViewById(R.id.textView7);
            cover = itemView.findViewById(R.id.libImage2);
            imageButton = itemView.findViewById(R.id.imageButton);
            imageButton1 = itemView.findViewById(R.id.imageButton3);
            author = itemView.findViewById(R.id.author);
            buyLink = itemView.findViewById(R.id.buyLink);
            price = itemView.findViewById(R.id.price);
            title = itemView.findViewById(R.id.title1);

        }
    }
}
