import { CreditCardService } from './../../service/credit-card.service';
import { QrCodeService } from './../../service/qr-code.service';
import { Component, OnInit } from '@angular/core';


@Component({
  selector: 'app-qr-code',
  templateUrl: './qr-code.component.html',
  styleUrls: ['./qr-code.component.scss']
})
export class QrCodeComponent implements OnInit {

  constructor(private qrCodeService: QrCodeService, private creditCardService: CreditCardService) { }
  paymentId = ""
  qrCodeData
  imagePath = ""
  image
  next : Boolean = false
  email = ""
  bankName = ""

  ngOnInit(): void {
    var url = window.location.href;
    this.paymentId = url.split("/")[4]
    console.log(this.paymentId)

    this.qrCodeService.getQrCodeData(this.paymentId).subscribe(data => {
      this.qrCodeData = data
      console.log(JSON.stringify(this.qrCodeData))

      this.qrCodeService.getQrCodeImage(JSON.stringify(this.qrCodeData)).subscribe(data => {
        this.imagePath = data
      })
    })

  }

  nextStep(){
    this.next = !this.next
  }

  continue(){
    console.log(this.bankName)

    let body = {
      "cardHolderName": "",
      "pan": "",
      "mm": "",
      "yy": "",
      "cvv": "",
      "paymentId": this.paymentId,
      "qrCodePayment": true,
      "issuer": this.email,
      "bankName": this.bankName
    }

    this.creditCardService.validateIssuer(JSON.stringify(body)).subscribe(
      data => { 
        console.log(data)
        window.location.href = data
      }, err => {
        console.log(err)
        if (err.error.includes("http")) {
          window.location.href = err.error
        }
      }
    );
  }

}
