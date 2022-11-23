import { AuthService } from './../../service/auth.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

interface DisplayMessage {
  msgType: string;
  msgBody: string;
}

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent implements OnInit {

  form: FormGroup;
  notification: DisplayMessage;

  constructor(private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router) { }

  ngOnInit(): void {

    this.form = this.formBuilder.group({
      name: ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(64)])],
      email: ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(64)])],
      password: ['', Validators.compose([Validators.required, Validators.minLength(8), Validators.maxLength(64)])],
      reEnteredPassword: ['', Validators.compose([Validators.required, Validators.minLength(8), Validators.maxLength(64)])],
      isCompany: [false, Validators.compose([])]

    });

  }

  submit(){
    if(this.form.valid){

      this.authService.signup(this.form.value)
      .subscribe(data => {
        console.log(data)
        this.router.navigate(['/personal-info']);
      },
      error => {
        this.notification = {msgType: 'error', msgBody: 'Something went wrong!'};
      });
    }else{
      this.notification = {msgType: 'error', msgBody: 'Please fill all field!'};
    }  }

}
