import { Routes } from '@angular/router';

import { AuthWrapperComponent } from './auth/auth-wrapper/auth-wrapper.component';
import { APP_ROUTES } from './common/const.data';
import { FollowersComponent } from './follower/followers/followers.component';
import { FollowingComponent } from './follower/following/following.component';
import MyFollowersResolver from './follower/resolver/my-followers.resolver';
import MyFollowingResolver from './follower/resolver/my-following.resolver';
import { FollowingPostsComponent } from './post/following-posts/following-posts.component';
import { MyPostsComponent } from './post/my-posts/my-posts.component';
import { PostWrapperComponent } from './post/post-wrapper/post-wrapper.component';
import FollowingResolver from './post/resolver/following-posts.resolver';
import MyPostsResolver from './post/resolver/my-posts.resolver';

const routes: Routes = [
  // TODO: Delete (?)
  // { path: '', redirectTo: APP_ROUTES.auth, pathMatch: 'full' },
  {
    path: '',
    component: FollowingPostsComponent,
    // canActivate: [AuthGuard],
    resolve: { posts: FollowingResolver },
  },
  { path: APP_ROUTES.auth, component: AuthWrapperComponent },
  {
    path: APP_ROUTES.post.posts,
    component: MyPostsComponent,
    // canActivate: [AuthGuard],
    resolve: { posts: MyPostsResolver },
  },
  {
    path: APP_ROUTES.post.editor,
    component: PostWrapperComponent,
    // canActivate: [AuthGuard],
  },
  {
    path: APP_ROUTES.follower.followers,
    component: FollowersComponent,
    // canActivate: [AuthGuard],
    resolve: { followers: MyFollowersResolver },
  },
  {
    path: APP_ROUTES.follower.following,
    component: FollowingComponent,
    // canActivate: [AuthGuard],
    resolve: { following: MyFollowingResolver },
  },
  { path: '**', component: AuthWrapperComponent },
];
export default routes;
