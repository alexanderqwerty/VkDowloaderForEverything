package org.example;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.GroupAuthResponse;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.messages.HistoryAttachment;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetHistoryAttachmentsQuery;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.apache.poi.ss.formula.atp.Switch;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;


public class Main {
    static String accessToken = "4da688544537d33cd6f120a4cef3e678b84074bae59038ed9729db382f697395c418802676078551790e7";
    //static String fuck =
    static Integer groupid = 213452188;

    public static void main(String[] args) throws ClientException, ApiException, InterruptedException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);

        GroupActor a = new GroupActor(groupid, accessToken);
        System.out.println(vk.messages().getLongPollServer(a).execute().toString());
        Integer ts = vk.messages().getLongPollServer(a).execute().getTs();
        Random random = new Random();
        while (true) {
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(a).ts(ts);
            MessagesGetHistoryAttachmentsQuery attachmentsQuery = vk.messages().getHistoryAttachments(a, -groupid);

            List<Message> messages = historyQuery.execute().getMessages().getItems();

            if (!messages.isEmpty()) {
                messages.forEach(message -> {
                    message.getAttachments().forEach(attachment -> {
                        URL url = null;
                        try {
                            switch (attachment.getType()) {
                                case PHOTO -> {
                                    System.out.println(attachment.getPhoto().getSizes().get(attachment.getPhoto().getSizes().size()-1).getUrl());
                                    url = new URL(attachment.getPhoto().getSizes().get(attachment.getPhoto().getSizes().size()-1).getUrl().toString());
                                    Files.copy(url.openStream(),Paths.get("C:\\Users\\duduc\\OneDrive\\Рабочий стол\\" + attachment.getPhoto().getId()+".jpeg"),StandardCopyOption.REPLACE_EXISTING);
                                    break;
                                }
                                case AUDIO -> {
                                    System.out.println(attachment.getAudio().getUrl());
                                    url = new URL(attachment.getAudio().getUrl().toString());
                                    Files.copy(url.openStream(),Paths.get("C:\\Users\\duduc\\OneDrive\\Рабочий стол\\" + attachment.getAudio().getTitle()+".mp3"),StandardCopyOption.REPLACE_EXISTING);
                                    break;
                                }
                                case VIDEO -> {
//                                    System.out.println(attachment.getVideo());
//                                    url = new URL("https://vk.com/video_ext.php?oid=390073364&id=456239168&hash=1cd119acb795bc2f");
//                                    Files.copy(url.openStream(),Paths.get("C:\\Users\\duduc\\OneDrive\\Рабочий стол\\" + "mp3.mp4"),StandardCopyOption.REPLACE_EXISTING);

                                    break;
                                }
                                case DOC -> {
                                    System.out.println(attachment.getDoc().getUrl());
                                    url = new URL(attachment.getDoc().getUrl().toString());
                                    Files.copy(url.openStream(),Paths.get("C:\\Users\\duduc\\OneDrive\\Рабочий стол\\" + attachment.getDoc().getTitle()),StandardCopyOption.REPLACE_EXISTING);
                                    break;
                                }
                                case LINK -> {
                                }
                                case MARKET -> {
                                }
                                case MARKET_ALBUM -> {
                                }
                                case GIFT -> {
                                }
                                case STICKER -> {
                                }
                                case WALL -> {
                                }
                                case WALL_REPLY -> {
                                }
                                case ARTICLE -> {
                                }
                                case POLL -> {
                                }
                                case CALL -> {
                                }
                                case GRAFFITI -> {
                                }
                                case AUDIO_MESSAGE -> {
                                    System.out.println(attachment.getAudioMessage().getLinkMp3());
                                    url = new URL(attachment.getAudioMessage().getLinkMp3().toString());
                                    Files.copy(url.openStream(),Paths.get("C:\\Users\\duduc\\OneDrive\\Рабочий стол\\" + attachment.getAudioMessage().getId()+".mp3"),StandardCopyOption.REPLACE_EXISTING);
                                    break;
                                }
                            }
                        }catch (IOException e){

                        }
                    });
//                        URL url = new URL(message.getAttachments().get(message.getAttachments().size() - 1).getDoc().getUrl().toString());
//                        InputStream in = url.openStream();
//                        message.getAttachments().forEach(attachment -> {
//                            System.out.println(attachment.getDoc().getTitle());
//                            vk.messages().send(a).message(attachment.getDoc().getTitle() + " Скачался").randomId(random.nextInt(10000)).userId(message.getFromId()).execute();
//                            Files.copy(in, Paths.get("C:\\Users\\duduc\\OneDrive\\Рабочий стол\\" + attachment.getDoc().getTitle()), StandardCopyOption.REPLACE_EXISTING);
//                        });

                });
            }
            ts = vk.messages().getLongPollServer(a).execute().getTs();
            Thread.sleep(500);
        }
    }
}
