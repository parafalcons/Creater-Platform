package com.platform.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.notification.service.NotificationService;
import com.platform.post.domain.Comment;
import com.platform.post.domain.Like;
import com.platform.post.domain.Post;
import com.platform.post.repository.CommentRepository;
import com.platform.post.repository.LikeRepository;
import com.platform.post.repository.PostRepository;
import com.platform.post.request.CreatePostRequest;
import com.platform.post.response.CommentResponse;
import com.platform.post.response.PostResponse;
import com.platform.user.domain.User;
import com.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        User user = userService.getUserByUserId(userId);
        
        Post post = Post.builder()
                .userId(userId)
                .userName(user.getUserName())
                .userFullName(user.getFullName())
                .userProfilePhoto(null)
                .imageUrl(request.getImageUrl())
                .caption(request.getCaption())
                .location(request.getLocation() != null ? request.getLocation() : "")
                .likesCount(0L)
                .commentsCount(0L)
                .isLikedByCurrentUser(false)
                .build();
        
        post = postRepository.save(post);
        return convertToResponse(post, userId);
    }

    @Override
    public PostResponse getPostById(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        boolean isLiked = likeRepository.existsByUserIdAndTargetIdAndTargetType(
                currentUserId, postId, "POST");
        post.setLikedByCurrentUser(isLiked);
        
        return convertToResponse(post, currentUserId);
    }

    @Override
    public List<PostResponse> getUserPosts(Long userId, Long currentUserId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(post -> {
                    boolean isLiked = likeRepository.existsByUserIdAndTargetIdAndTargetType(
                            currentUserId, post.getId(), "POST");
                    post.setLikedByCurrentUser(isLiked);
                    return convertToResponse(post, currentUserId);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getFeedPosts(Long currentUserId, int page, int size) {
        List<Long> followingUserIds = userService.getUserConnections(currentUserId, false)
                .stream()
                .map(conn -> conn.getConnectionId())
                .collect(Collectors.toList());
        
        followingUserIds.add(currentUserId);
        
        List<Post> posts = postRepository.findByUserIdsOrderByCreatedAtDesc(followingUserIds);
        
        return posts.stream()
                .map(post -> {
                    boolean isLiked = likeRepository.existsByUserIdAndTargetIdAndTargetType(
                            currentUserId, post.getId(), "POST");
                    post.setLikedByCurrentUser(isLiked);
                    return convertToResponse(post, currentUserId);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> searchPosts(String searchTerm, Long currentUserId) {
        List<Post> posts = postRepository.searchPosts(searchTerm);
        return posts.stream()
                .map(post -> {
                    boolean isLiked = likeRepository.existsByUserIdAndTargetIdAndTargetType(
                            currentUserId, post.getId(), "POST");
                    post.setLikedByCurrentUser(isLiked);
                    return convertToResponse(post, currentUserId);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        User user = userService.getUserByUserId(userId);
        
        if (likeRepository.existsByUserIdAndTargetIdAndTargetType(userId, postId, "POST")) {
            return false;
        }
        
        Like like = Like.builder()
                .userId(userId)
                .userName(user.getUserName())
                .userFullName(user.getFullName())
                .userProfilePhoto(null)
                .targetId(postId)
                .targetType("POST")
                .build();
        
        likeRepository.save(like);
        
        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);
        
        if (!post.getUserId().equals(userId)) {
            notificationService.createNotification(
                    post.getUserId(), userId, "LIKE", 
                    user.getUserName() + " liked your post", 
                    postId, "POST", "/post/" + postId);
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!likeRepository.existsByUserIdAndTargetIdAndTargetType(userId, postId, "POST")) {
            return false;
        }
        
        likeRepository.deleteByUserIdAndTargetIdAndTargetType(userId, postId, "POST");
        
        post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        postRepository.save(post);
        
        return true;
    }

    @Override
    @Transactional
    public boolean deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }
        
        likeRepository.deleteByTargetIdAndTargetType(postId, "POST");
        commentRepository.deleteByPostId(postId);
        postRepository.delete(post);
        
        return true;
    }

    private PostResponse convertToResponse(Post post, Long currentUserId) {
        PostResponse response = new PostResponse();
        BeanUtils.copyProperties(post, response);
        
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(post.getId());
        List<CommentResponse> commentResponses = comments.stream()
                .map(this::convertCommentToResponse)
                .collect(Collectors.toList());
        
        response.setComments(commentResponses);
        return response;
    }

    private CommentResponse convertCommentToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        BeanUtils.copyProperties(comment, response);
        return response;
    }
} 