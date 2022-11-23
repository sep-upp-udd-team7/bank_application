import { AuthService } from './../../service/auth.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

interface DisplayMessage {
  msgType: string;
  msgBody: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  form: FormGroup;
  notification: DisplayMessage;


  constructor(private router: Router,
    private formBuilder: FormBuilder,
    private authService: AuthService
    ) { }

  ngOnInit(): void {
    
    this.form = this.formBuilder.group({
      email: ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(64)])],
      password: ['', Validators.compose([Validators.required, Validators.minLength(8), Validators.maxLength(64)])]
    });
  }

  onSubmit(){
    if(this.form.valid){

      this.authService.login(this.form.value)
      .subscribe(data => {
        console.log(data)
        this.router.navigate(['/personal-info']);
      },
      error => {
        this.notification = {msgType: 'error', msgBody: 'Invalid email or password!'};
      });
    }else{
      this.notification = {msgType: 'error', msgBody: 'Password must be 8 or more characters!'};
    }
  }

  signup(){
    this.router.navigate(['/signup']);
  }

}
