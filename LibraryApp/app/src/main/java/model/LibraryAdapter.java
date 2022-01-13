package model;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.BooksActivity;
import com.example.libraryapp.LibraryActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.repo.BookRepo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.BookViewHolder> implements Filterable {
    private static final String TAG = "Out";
    List<Book> bookList;
    List <Book> bookListCopy = new ArrayList<>();
    CoordinatorLayout layout;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    BookRepo bookRepo = new BookRepo();

    //There's an index out of bound error

    public LibraryAdapter(List<Book> bookList1,List <Book> bookListCopy) {

        this.bookList=bookList1;
        this.bookListCopy.addAll(bookList1);

    }

    @NonNull
    @Override
    public LibraryAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout for item of recycler view item.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycler_layout, parent, false);
        LibraryAdapter.BookViewHolder viewHolder = new LibraryAdapter.BookViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.BookViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // inside on bind view holder method we are
        // setting ou data to each UI component.
        Book book = bookList.get(position);
        holder.name.setText((book.getTitle()));
        String aUrl = book.getThumbnail().replace("http", "https");
        //Image isnt loading
        if(!aUrl.isEmpty())
            Picasso.get()
                .load(aUrl)
                .fit()
                .placeholder(R.mipmap.ic_launcher)  // preload
                .error(R.mipmap.ic_launcher_error)        // load error
                .into(holder.cover);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout = v.findViewById(R.id.layout);
                if(bookRepo.remove(book, mUser.getUid())){
                    Snackbar mySnackbar1 = Snackbar.make(v, "The books been removed!", Snackbar.LENGTH_LONG);
                    mySnackbar1.show();
                    LibraryActivity.mAdapter.remove(position);


                    System.out.println(LibraryActivity.mAdapter.bookList.size());

                }
            }
        });
        holder.aSwitch.setChecked(book.getStatus());
        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean status = holder.aSwitch.isChecked();
                bookRepo.updateBookStatus(book,status, mUser.getUid());

            }
        });
    }

    Filter filter =  new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            System.out.println(bookList.size());

            List<Book> filteredList = new ArrayList<>();
            System.out.println(bookList.size() + "BookCopy og");
            System.out.println(bookListCopy.size() + "BookCopy");

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(bookListCopy);
                for(Book b : bookList){
                    System.out.println(b.getTitle());
                }
            } else {
                for (Book book : bookListCopy) {
                    if (book.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(book);

//                            System.out.println(book.getTitle());

                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;


            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            bookList.clear();
            //myBookList is made empty and the results of the filter are placed in it
            bookList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {

           return bookList.size();

    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    //ViewHolder class
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView name;
        ImageButton imageButton;
        Switch aSwitch;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.libname);
            cover = itemView.findViewById(R.id.libImage2);
            imageButton = itemView.findViewById(R.id.libImage);
            aSwitch = itemView.findViewById(R.id.switch1);


        }
    }
    public void remove(int position){
        //bookList is already removing
        bookList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        notifyDataSetChanged();

    }

    public void update(Book book, int position){
        bookList.set(position, book);
        notifyItemChanged(position);
    }
}

//when i delete from the filtered list it doesnt immediately delete from the other list,
//im wondering why