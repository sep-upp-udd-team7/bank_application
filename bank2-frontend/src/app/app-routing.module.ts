import { CardInfoComponent } from './components/card-info/card-info.component';
import { PersonalInfoComponent } from './components/personal-info/personal-info.component';
import { SignUpComponent } from './components/sign-up/sign-up.component';
import { LoginComponent } from './components/login/login.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    component: LoginComponent,
    pathMatch: 'full', 
  },
  
  {
    path: 'signup',
    component: SignUpComponent,
  },
  {
    path: 'personal-info',
    component: PersonalInfoComponent,
  },
  {
    path: 'credit-card/:paymentId',
    component: CardInfoComponent,
  },
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})


export class AppRoutingModule { }
