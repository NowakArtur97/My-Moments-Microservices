import { Routes } from '@angular/router';

import { AuthWrapperComponent } from './auth/auth-wrapper/auth-wrapper.component';
import { APP_ROUTES } from './common/const.data';
import { PostWrapperComponent } from './post/post-wrapper/post-wrapper.component';

const routes: Routes = [
  { path: '', redirectTo: APP_ROUTES.auth, pathMatch: 'full' },
  { path: APP_ROUTES.auth, component: AuthWrapperComponent },
  {
    path: 'posts',
    component: PostWrapperComponent,
    // canActivate: [AuthGuard],
  },
  { path: 'new', component: PostWrapperComponent },
  { path: '**', component: AuthWrapperComponent },
];
export default routes;
