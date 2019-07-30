import scala.collection.JavaConverters._
import java.net.URLDecoder
import java.util.{Date,TimeZone}
import java.text.SimpleDateFormat

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model._
import java.util

import config.PipelineConfig

object JPYSnsMain {
  def main(args: Array[String]): Unit = {
    val topicArn = PipelineConfig.TOPIC_ARN
    val access_key = PipelineConfig.ACCESS_KEY
    val secret_key = PipelineConfig.SECRET_KEY
    val url = PipelineConfig.BANK_URL

    //get date and format
    val now = new Date()
    val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"))
    val today = formatter.format(now)

    //create AWS SNS Client
    val snsClient: AmazonSNSClient= new AmazonSNSClient(new BasicAWSCredentials(access_key, secret_key))

    //get value from webpage and extract target value
    val data = Jsoup.connect(url).get()
    val table = data.select("table").first()
    val price_array = table.select("td.rate-content-sight").text.replaceAll("-", "0.0").split(" ").map(_.toFloat)
    val usd = price_array(1)
    val hkd = price_array(3)
    val aud = price_array(5)
    val jpy = price_array(15)
    //price_array.foreach(x => println(x.toString))

    if (usd < 30) {
      val message = s"It's a good time to buy USD, USD price is $usd, Date time is $today."
      sendSMSMessageToTopic(snsClient, topicArn, message)
      //println(message)
    } else if (hkd < 3.8) {
      val message = s"It's a good time to buy HKD, HKD price is $hkd, Date time is $today."
      sendSMSMessageToTopic(snsClient, topicArn, message)
    } else if (jpy > 0.29) {
      val message = s"JPY price is over than 0.29, JPY price is $jpy, Date time is $today."
      sendSMSMessageToTopic(snsClient, topicArn, message)
    } else if (jpy <= 0.273) {
      val message = s"It's a good time to buy JPY, JPY price is $jpy, Date time is $today."
      sendSMSMessageToTopic(snsClient, topicArn, message)
    }

    //sendSMSMessageToTopic(snsClient, topicArn, message)

    def sendSMSMessageToTopic(snsClient: AmazonSNSClient, topicArn: String, message: String) = {
      val result = snsClient.publish(new PublishRequest().withTopicArn(topicArn).withMessage(message))
      result.getMessageId
    }




  }
}
