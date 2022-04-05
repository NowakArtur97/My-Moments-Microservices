import { Routes } from '@angular/router';

import { AuthWrapperComponent } from './auth/auth-wrapper/auth-wrapper.component';
import { AuthGuard } from './auth/auth.guard';
import { ROUTES } from './common/const.data';
import { PostWrapperComponent } from './post/post-wrapper/post-wrapper.component';

const routes: Routes = [
  { path: '', redirectTo: ROUTES.auth, pathMatch: 'full' },
  { path: ROUTES.auth, component: AuthWrapperComponent },
  {
    path: 'posts',
    component: PostWrapperComponent,
    canActivate: [AuthGuard],
  },
  { path: '**', component: AuthWrapperComponent },
];
export default routes;
