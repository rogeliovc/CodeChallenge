package com.example.codechallenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment {
    private RecyclerView recyclerView;
    private ForumAdapter adapter;
    private List<ForumPost> posts;
    private FirebaseFirestore db;
    private android.widget.TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        recyclerView = view.findViewById(R.id.recyclerForum);
        FloatingActionButton fab = view.findViewById(R.id.fabNewPost);
        emptyView = new android.widget.TextView(getContext());
        emptyView.setText("No hay posts en el foro");
        emptyView.setTextSize(18);
        emptyView.setTextColor(0xFF888888);
        emptyView.setGravity(android.view.Gravity.CENTER);
        ((androidx.constraintlayout.widget.ConstraintLayout) view).addView(emptyView);
        emptyView.setVisibility(android.view.View.GONE);
        posts = new ArrayList<>();
        adapter = new ForumAdapter(posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        loadPosts();
        fab.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setTitle("Nuevo post");
            final android.widget.EditText input = new android.widget.EditText(getContext());
            input.setHint("¿Qué quieres preguntar o compartir?");
            builder.setView(input);
            builder.setPositiveButton("Publicar", (dialog, which) -> {
                String content = input.getText().toString().trim();
                if (content.isEmpty()) {
                    android.widget.Toast.makeText(getContext(), "El contenido no puede estar vacío", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                // Obtén usuario actual
                com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    android.widget.Toast.makeText(getContext(), "Debes iniciar sesión", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                ForumPost post = new ForumPost(
                    user.getUid(),
                    user.getDisplayName() != null ? user.getDisplayName() : user.getEmail(),
                    content,
                    com.google.firebase.Timestamp.now()
                );
                db.collection("forum_posts").add(post)
                    .addOnSuccessListener(ref -> {
                        android.widget.Toast.makeText(getContext(), "¡Post publicado!", android.widget.Toast.LENGTH_SHORT).show();
                        // Forzar refresco inmediato
                        loadPosts();
                    })
                    .addOnFailureListener(e -> android.widget.Toast.makeText(getContext(), "Error al publicar", android.widget.Toast.LENGTH_SHORT).show());
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        return view;
    }

    private void loadPosts() {
        CollectionReference forumRef = db.collection("forum_posts");
        forumRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;
                        posts.clear();
                        if (snapshots != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                                ForumPost post = doc.toObject(ForumPost.class);
                                posts.add(post);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (posts.isEmpty()) {
                            recyclerView.setVisibility(android.view.View.GONE);
                            emptyView.setVisibility(android.view.View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(android.view.View.VISIBLE);
                            emptyView.setVisibility(android.view.View.GONE);
                        }
                    }
                });
    }

    private class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumViewHolder> {
        private final List<ForumPost> posts;
        ForumAdapter(List<ForumPost> posts) { this.posts = posts; }
        @NonNull
        @Override
        public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_post, parent, false);
            return new ForumViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
            ForumPost post = posts.get(position);
            holder.author.setText(post.getAuthor_name());
            holder.content.setText(post.getContent());
            String time = "";
            if (post.getTimestamp() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale.getDefault());
                time = sdf.format(post.getTimestamp().toDate());
            }
            holder.time.setText(time);
            // Likes
            holder.likeCount.setText(String.valueOf(post.getLikes()));
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            final String currentUid = (user != null) ? user.getUid() : null;
            boolean liked = post.getLikedBy() != null && currentUid != null && post.getLikedBy().contains(currentUid);
            holder.btnLike.setColorFilter(liked ? 0xFF1976D2 : 0xFF888888);
            holder.btnLike.setOnClickListener(v2 -> {
                if (currentUid == null) {
                    android.widget.Toast.makeText(getContext(), "Debes iniciar sesión para dar like", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                com.google.firebase.firestore.DocumentReference ref = db.collection("forum_posts").document(post.getId());
                db.runTransaction(transaction -> {
                    com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(ref);
                    java.util.List<String> likedBy = (java.util.List<String>) snapshot.get("likedBy");
                    int likes = snapshot.getLong("likes") != null ? snapshot.getLong("likes").intValue() : 0;
                    if (likedBy == null) likedBy = new java.util.ArrayList<>();
                    if (likedBy.contains(currentUid)) {
                        likedBy.remove(currentUid);
                        likes = Math.max(0, likes - 1);
                    } else {
                        likedBy.add(currentUid);
                        likes++;
                    }
                    transaction.update(ref, "likedBy", likedBy, "likes", likes);
                    return null;
                });
            });
            holder.itemView.setOnClickListener(v -> {
                // Abre el detalle del post (comentarios)
                androidx.fragment.app.FragmentActivity activity = getActivity();
                if (activity != null) {
                    ForumDetailFragment detail = ForumDetailFragment.newInstance(post.getId());
                    activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detail)
                        .addToBackStack(null)
                        .commit();
                }
            });
        }
        @Override
        public int getItemCount() { return posts.size(); }
        class ForumViewHolder extends RecyclerView.ViewHolder {
            TextView author, content, time, likeCount;
            android.widget.ImageButton btnLike;
            ForumViewHolder(View itemView) {
                super(itemView);
                author = itemView.findViewById(R.id.txtPostAuthor);
                content = itemView.findViewById(R.id.txtPostContent);
                time = itemView.findViewById(R.id.txtPostTime);
                likeCount = itemView.findViewById(R.id.txtLikeCount);
                btnLike = itemView.findViewById(R.id.btnLike);
            }
        }
    }
}
