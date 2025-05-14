package com.example.codechallenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ForumDetailFragment extends Fragment {
    private static final String ARG_POST_ID = "post_id";
    private String postId;
    private ForumPost post;
    private TextView txtPostContent;
    private RecyclerView recyclerComments;
    private EditText editComment;
    private Button btnSendComment;
    private CommentsAdapter adapter;
    private List<ForumComment> comments;
    private FirebaseFirestore db;

    public static ForumDetailFragment newInstance(String postId) {
        ForumDetailFragment fragment = new ForumDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_detail, container, false);
        txtPostContent = view.findViewById(R.id.txtPostContent);
        recyclerComments = view.findViewById(R.id.recyclerComments);
        editComment = view.findViewById(R.id.editComment);
        btnSendComment = view.findViewById(R.id.btnSendComment);
        comments = new ArrayList<>();
        adapter = new CommentsAdapter(comments);
        recyclerComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerComments.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
        }
        loadPost();
        loadComments();
        btnSendComment.setOnClickListener(v -> sendComment());
        return view;
    }

    private void loadPost() {
        db.collection("forum_posts").document(postId).get().addOnSuccessListener(doc -> {
            post = doc.toObject(ForumPost.class);
            if (post != null) {
                String time = "";
                if (post.getTimestamp() != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale.getDefault());
                    time = sdf.format(post.getTimestamp().toDate());
                }
                String html = "<b>" + post.getAuthor_name() + "</b> <small style='color:#888;'>" + time + "</small><br>" + post.getContent();
                txtPostContent.setText(android.text.Html.fromHtml(html));
            }
        });
    }

    private void loadComments() {
        CollectionReference commentsRef = db.collection("forum_posts").document(postId).collection("comments");
        commentsRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;
                        comments.clear();
                        if (snapshots != null) {
                            for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                                ForumComment comment = doc.toObject(ForumComment.class);
                                comments.add(comment);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void sendComment() {
        String content = editComment.getText().toString().trim();
        if (content.isEmpty()) {
            Snackbar.make(editComment, "El comentario no puede estar vacío", Snackbar.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Snackbar.make(editComment, "Debes iniciar sesión", Snackbar.LENGTH_SHORT).show();
            return;
        }
        ForumComment comment = new ForumComment(
                user.getUid(),
                user.getDisplayName() != null ? user.getDisplayName() : user.getEmail(),
                content,
                Timestamp.now()
        );
        DocumentReference commentRef = db.collection("forum_posts").document(postId)
                .collection("comments").document();
        commentRef.set(comment)
                .addOnSuccessListener(aVoid -> {
                    editComment.setText("");
                    Snackbar.make(editComment, "¡Comentario publicado!", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Snackbar.make(editComment, "Error al publicar", Snackbar.LENGTH_SHORT).show());
    }

    private static class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
        private final List<ForumComment> comments;
        CommentsAdapter(List<ForumComment> comments) { this.comments = comments; }
        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            ForumComment comment = comments.get(position);
            holder.author.setText(comment.getAuthor_name());
            holder.content.setText(comment.getContent());
            String time = "";
            if (comment.getTimestamp() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale.getDefault());
                time = sdf.format(comment.getTimestamp().toDate());
            }
            holder.time.setText(time);
        }
        @Override
        public int getItemCount() { return comments.size(); }
        static class CommentViewHolder extends RecyclerView.ViewHolder {
            TextView author, content, time;
            CommentViewHolder(View itemView) {
                super(itemView);
                author = itemView.findViewById(R.id.txtCommentAuthor);
                content = itemView.findViewById(R.id.txtCommentContent);
                time = itemView.findViewById(R.id.txtCommentTime);
            }
        }
    }
}
