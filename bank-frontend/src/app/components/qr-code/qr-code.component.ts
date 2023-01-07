import { QrCodeService } from './../../service/qr-code.service';
import { Component, OnInit } from '@angular/core';


@Component({
  selector: 'app-qr-code',
  templateUrl: './qr-code.component.html',
  styleUrls: ['./qr-code.component.scss']
})
export class QrCodeComponent implements OnInit {

  constructor(private qrCodeService: QrCodeService) { }
  paymentId = ""
  qrCodeData
  imagePath = ""
  image

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

}
