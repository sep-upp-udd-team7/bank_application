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

  ngOnInit(): void {
    this.authService.getLoggedUser().subscribe(data => {
      this.client = data
      this.pan = this.client.bankAccount.creditCard.pan;
      let tempPan = this.pan.substring(0, 4) + "xxxxxxxxxxxx"
      this.client.bankAccount.creditCard.pan = tempPan;
      if (this.client.merchantId != "") {
        this.isCompany = true;
      } else {
        this.isCompany = false;
      }
      console.log(data)
    })

  }

}
