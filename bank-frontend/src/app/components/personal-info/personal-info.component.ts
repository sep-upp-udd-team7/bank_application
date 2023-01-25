import { Component, OnInit } from '@angular/core';
import { Client } from 'src/app/model/client.model';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-personal-info',
  templateUrl: './personal-info.component.html',
  styleUrls: ['./personal-info.component.scss']
})
export class PersonalInfoComponent implements OnInit {

  constructor(private authService: AuthService) { }
  client: Client;
  isCompany: boolean;
  pan: string = '';
  panIsVisible: boolean = false;

  ngOnInit(): void {
    this.authService.getLoggedUser().subscribe(data => {
      this.client = data
      this.pan = this.client.bankAccount.creditCard.pan;
      this.client.bankAccount.creditCard.pan = this.hidePan(this.pan);
      if (this.client.merchantId != "") {
        this.isCompany = true;
      } else {
        this.isCompany = false;
      }
      console.log(data)
    })

  }

  visiblityPan: string = 'visibility_on';
  changeVisibilityOfPan() {
    this.panIsVisible = !this.panIsVisible;
    this.changeButtonIcon(this.panIsVisible);
    if(this.panIsVisible) {
      this.client.bankAccount.creditCard.pan = this.pan
    } else {
      this.client.bankAccount.creditCard.pan = this.hidePan(this.pan)
    }
    
  }

  hidePan(pan: string) {
    return pan.substring(0, 4) + "xxxxxxxxxxxx"
  }

  changeButtonIcon(panIsVisible: boolean) {
    if (panIsVisible) {
      this.visiblityPan = 'visibility_off';
    } else {
      this.visiblityPan = 'visibility_on';
    }
  }
}
