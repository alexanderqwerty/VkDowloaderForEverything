package org.example;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;


import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;


public class Main {
    static String accessToken = "4da688544537d33cd6f120a4cef3e678b84074bae59038ed9729db382f697395c418802676078551790e7";
    static String fuck = "Иди нахуй ваня";
    static String director = "C:\\Users\\duduc\\OneDrive\\Рабочий стол";
    static Integer groupid = 213452188;
    static Integer ivan = 397698080;
    static Integer my = 397698080;

    public static void getPhoto(MessageAttachment attachment) throws IOException {
        System.out.println(attachment.getPhoto().getSizes().get(attachment.getPhoto().getSizes().size() - 1).getUrl());
        URL url = new URL(attachment.getPhoto().getSizes().get(attachment.getPhoto().getSizes().size() - 1).getUrl().toString());
        Files.copy(url.openStream(), Paths.get(director + attachment.getPhoto().getId() + ".jpeg"), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void getVideo(VkApiClient vk, GroupActor a) throws IOException, ClientException, ApiException {
        vk.messages().send(a)
                .message("Не поддерживается скачивание видео")
                .randomId(new Random().nextInt(10000))
                .execute();
    }

    public static void getAudio(MessageAttachment attachment) throws IOException {
        System.out.println(attachment.getAudio().getUrl());
        URL url = new URL(attachment.getAudio().getUrl().toString());
        Files.copy(url.openStream(), Paths.get(director + attachment.getAudio().getTitle() + ".mp3"), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void getDoc(MessageAttachment attachment) throws IOException {
        System.out.println(attachment.getDoc().getUrl());
        URL url = new URL(attachment.getDoc().getUrl().toString());
        Files.copy(url.openStream(), Paths.get(director + attachment.getDoc().getTitle()), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void getGraffiti(MessageAttachment attachment) throws IOException {
        System.out.println(attachment.getGraffiti().getUrl());
        URL url = new URL(attachment.getGraffiti().getUrl().toString());
        Files.copy(url.openStream(), Paths.get(director + attachment.getGraffiti().getId() + ".png"), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void getAudioMessage(MessageAttachment attachment) throws IOException {
        System.out.println(attachment.getAudioMessage().getLinkMp3());
        URL url = new URL(attachment.getAudioMessage().getLinkMp3().toString());
        Files.copy(url.openStream(), Paths.get(director + attachment.getAudioMessage().getId() + ".mp3"), StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean hasReplyMessage(Message message) {
        try {
            return !message.getReplyMessage().getAttachments().isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void main(String[] args) throws ClientException, ApiException, InterruptedException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        Random random = new Random();
        GroupActor a = new GroupActor(groupid, accessToken);
        System.out.println(vk.messages().getLongPollServer(a).execute().toString());
        Integer ts = vk.messages().getLongPollServer(a).execute().getTs();

        while (true) {
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(a).ts(ts);
            List<Message> messages = historyQuery.execute().getMessages().getItems();
            if (!messages.isEmpty()) {
                messages.forEach(message -> {
                    if (!message.getAttachments().isEmpty())
                        message.getAttachments().forEach(attachment -> {
                            try {
                                switch (attachment.getType()) {
                                    case PHOTO -> getPhoto(attachment);
                                    case AUDIO -> getAudio(attachment);
                                    case VIDEO -> getVideo(vk, a);
                                    case DOC -> getDoc(attachment);
                                    case GRAFFITI -> getGraffiti(attachment);
                                    case AUDIO_MESSAGE -> getAudioMessage(attachment);
                                    default -> vk.messages().send(a).userId(message.getId())
                                            .randomId(random.nextInt(10000))
                                            .message("Пока не поддерживается")
                                            .execute();
                                }
                            } catch (IOException | ApiException | ClientException e) {

                            }

                        });
                    else if (hasReplyMessage(message)) {
                        message.getReplyMessage().getAttachments().forEach(attachment -> {
                            try {
                                switch (attachment.getType()) {
                                    case PHOTO -> getPhoto(attachment);
                                    case AUDIO -> getAudio(attachment);
                                    case VIDEO -> getVideo(vk, a);
                                    case DOC -> getDoc(attachment);
                                    case GRAFFITI -> getGraffiti(attachment);
                                    case AUDIO_MESSAGE -> getAudioMessage(attachment);
                                    default -> vk.messages().send(a).userId(message.getId())
                                            .randomId(random.nextInt(10000))
                                            .message("Пока не поддерживается")
                                            .execute();
                                }
                            } catch (IOException | ApiException | ClientException e) {

                            }

                        });
                    } else {
                        System.out.println(message.getText());
                    }
                });
            }
            ts = vk.messages().getLongPollServer(a).execute().getTs();
            Thread.sleep(500);
        }
    }
}
