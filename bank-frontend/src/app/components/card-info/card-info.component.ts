import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CreditCardService } from 'src/app/service/credit-card.service';

@Component({
  selector: 'app-card-info',
  templateUrl: './card-info.component.html',
  styleUrls: ['./card-info.component.scss']
})
export class CardInfoComponent implements OnInit {

  constructor(private route: ActivatedRoute, private creditCardService: CreditCardService) { }
  paymentId: string = "";

  name: string = "";
  pan: string = "";
  mm: string = "";
  yy: string = "";
  cvv: string = "";

  years: string[] = [];
  months: string[] = [];

  ngOnInit(): void {
    this.paymentId = JSON.parse(this.route.snapshot.paramMap.get('paymentId') as string);
    // alert(this.paymentId)
    
    this.setYearOptions(10);
    this.setMonthOptions(12);
  }


  private setMonthOptions(maxMountNumber: number) {
    for (let i = 1; i <= maxMountNumber; i++) {
      this.months.push(i.toString());
    }
    this.mm = "1";
  }

  private setYearOptions(howManyYearsInFuture: number) {
    let currentYear = new Date().getFullYear();
    for (let i = 0; i <= howManyYearsInFuture; i++) {
      let num = currentYear + i;
      this.years.push(num.toString());
    }
    this.yy = currentYear.toString();
  }

  validateIssuer() {
    let body = {
      "cardHolderName": this.name,
      "pan": this.pan,
      "mm": this.mm,
      "yy": this.yy,
      "cvv": this.cvv,
      "paymentId": this.paymentId
    }

    this.creditCardService.validateIssuer(JSON.stringify(body)).subscribe(
      d =>{ 
      console.log(d)
      }
    );

    
  }
}
