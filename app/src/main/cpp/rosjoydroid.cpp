#include <ctime>
#include <string>

#include <jni.h>

#include <fastdds/dds/domain/DomainParticipant.hpp>
#include <fastdds/dds/domain/DomainParticipantFactory.hpp>
#include <fastdds/dds/publisher/DataWriter.hpp>
#include <fastdds/dds/publisher/Publisher.hpp>
#include <fastdds/dds/topic/TypeSupport.hpp>

#include "sensor_msgs/msg/JoyPubSubTypes.h"
#include "geometry_msgs/msg/PointPubSubTypes.h"

namespace fastdds {
    using namespace eprosima::fastdds::dds;
    using namespace eprosima::fastrtps::rtps;
}

class JoyPublisher {
public:
    sensor_msgs::msg::Joy joy_msg;

    JoyPublisher(uint32_t domain_id, const std::string &ns) : type_(
            new sensor_msgs::msg::JoyPubSubType()) {
        std::string topic_name = "rt/" + ns + (ns.empty() ? "" : "/") + "joy";
        fastdds::TopicQos qos;
        qos.history().depth = 10;
        joy_msg.header().frame_id() = "joy";

        participant_ =
                fastdds::DomainParticipantFactory::get_instance()->create_participant(domain_id,
                                                                                      fastdds::PARTICIPANT_QOS_DEFAULT);
        type_.register_type(participant_);
        topic_ = participant_->create_topic(topic_name, type_.get_type_name(), qos);
        publisher_ = participant_->create_publisher(fastdds::PUBLISHER_QOS_DEFAULT);

        fastdds::DataWriterQos writer_qos = fastdds::DATAWRITER_QOS_DEFAULT;
        writer_qos.publish_mode().kind = fastdds::ASYNCHRONOUS_PUBLISH_MODE;
        writer_qos.endpoint().history_memory_policy = fastdds::PREALLOCATED_WITH_REALLOC_MEMORY_MODE;
        writer_qos.data_sharing().off();
        writer_ = publisher_->create_datawriter(topic_, writer_qos);
    }

    ~JoyPublisher() {
        publisher_->delete_datawriter(writer_);
        participant_->delete_publisher(publisher_);
        participant_->delete_topic(topic_);
        fastdds::DomainParticipantFactory::get_instance()->delete_participant(participant_);
    }

    void publish() {
        writer_->write(&joy_msg);
    }

private:
    fastdds::DomainParticipant *participant_;
    fastdds::Publisher *publisher_;
    fastdds::Topic *topic_;
    fastdds::DataWriter *writer_;
    fastdds::TypeSupport type_;
};

class ControlPublisher {
public:
    sensor_msgs::msg::Joy control_msg;

    ControlPublisher(uint32_t domain_id, const std::string &ns) : type_(
            new sensor_msgs::msg::JoyPubSubType()) {
        std::string topic_name = "rt/" + ns + (ns.empty() ? "" : "/") + "control";
        fastdds::TopicQos qos;
        qos.history().depth = 10;
        control_msg.header().frame_id() = "control";

        participant_ =
                fastdds::DomainParticipantFactory::get_instance()->create_participant(domain_id,
                                                                                      fastdds::PARTICIPANT_QOS_DEFAULT);
        type_.register_type(participant_);
        topic_ = participant_->create_topic(topic_name, type_.get_type_name(), qos);
        publisher_ = participant_->create_publisher(fastdds::PUBLISHER_QOS_DEFAULT);

        fastdds::DataWriterQos writer_qos = fastdds::DATAWRITER_QOS_DEFAULT;
        writer_qos.publish_mode().kind = fastdds::ASYNCHRONOUS_PUBLISH_MODE;
        writer_qos.endpoint().history_memory_policy = fastdds::PREALLOCATED_WITH_REALLOC_MEMORY_MODE;
        writer_qos.data_sharing().off();
        writer_ = publisher_->create_datawriter(topic_, writer_qos);
    }

    ~ControlPublisher() {
        publisher_->delete_datawriter(writer_);
        participant_->delete_publisher(publisher_);
        participant_->delete_topic(topic_);
        fastdds::DomainParticipantFactory::get_instance()->delete_participant(participant_);
    }

    void publish() {
        writer_->write(&control_msg);
    }


private:
    fastdds::DomainParticipant *participant_;
    fastdds::Publisher *publisher_;
    fastdds::Topic *topic_;
    fastdds::DataWriter *writer_;
    fastdds::TypeSupport type_;
};

class TouchPublisher {
public:
    geometry_msgs::msg::Point point_msg;

    TouchPublisher(uint32_t domain_id, const std::string &ns) : type_(
            new geometry_msgs::msg::PointPubSubType()) {
        std::string topic_name = "rt/" + ns + (ns.empty() ? "" : "/") + "touch";
        fastdds::TopicQos qos;
        qos.history().depth = 10;

        participant_ =
                fastdds::DomainParticipantFactory::get_instance()->create_participant(domain_id,
                                                                                      fastdds::PARTICIPANT_QOS_DEFAULT);
        type_.register_type(participant_);
        topic_ = participant_->create_topic(topic_name, type_.get_type_name(), qos);
        publisher_ = participant_->create_publisher(fastdds::PUBLISHER_QOS_DEFAULT);

        fastdds::DataWriterQos writer_qos = fastdds::DATAWRITER_QOS_DEFAULT;
        writer_qos.publish_mode().kind = fastdds::ASYNCHRONOUS_PUBLISH_MODE;
        writer_qos.endpoint().history_memory_policy = fastdds::PREALLOCATED_WITH_REALLOC_MEMORY_MODE;
        writer_qos.data_sharing().off();
        writer_ = publisher_->create_datawriter(topic_, writer_qos);
    }

    ~TouchPublisher() {
        publisher_->delete_datawriter(writer_);
        participant_->delete_publisher(publisher_);
        participant_->delete_topic(topic_);
        fastdds::DomainParticipantFactory::get_instance()->delete_participant(participant_);
    }

    void publish() {
        writer_->write(&point_msg);
    }

private:
    fastdds::DomainParticipant *participant_;
    fastdds::Publisher *publisher_;
    fastdds::Topic *topic_;
    fastdds::DataWriter *writer_;
    fastdds::TypeSupport type_;
};

JoyPublisher *joy_publisher1 = nullptr;
JoyPublisher *joy_publisher2 = nullptr;
JoyPublisher *joy_service_publisher = nullptr;
ControlPublisher *control_publisher = nullptr;
TouchPublisher *touch_publisher = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_MainActivity_createJoyPublisher(JNIEnv *env, jobject thiz, jint domain_id,
                                                          jstring ns) {
    const char *ns_ = env->GetStringUTFChars(ns, nullptr);
    joy_publisher1 = new JoyPublisher(domain_id, ns_);
    env->ReleaseStringUTFChars(ns, ns_);
}

extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_MainActivity_destroyJoyPublisher(JNIEnv *env, jobject thiz) {
    delete joy_publisher1;
    joy_publisher1 = nullptr;
}

extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_MainActivity_publishJoy(JNIEnv *env, jobject thiz, jfloatArray axes,
                                                  jintArray buttons) {
    timespec now;
    clock_gettime(CLOCK_REALTIME, &now);
    joy_publisher1->joy_msg.header().stamp().sec() = now.tv_sec;
    joy_publisher1->joy_msg.header().stamp().nanosec() = now.tv_nsec;

    joy_publisher1->joy_msg.axes().resize(env->GetArrayLength(axes));
    joy_publisher1->joy_msg.buttons().resize(env->GetArrayLength(buttons));
    env->GetFloatArrayRegion(axes, 0, joy_publisher1->joy_msg.axes().size(),
                             joy_publisher1->joy_msg.axes().data());
    env->GetIntArrayRegion(buttons, 0, joy_publisher1->joy_msg.buttons().size(),
                           joy_publisher1->joy_msg.buttons().data());

    joy_publisher1->publish();
}


extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_TouchScreenActivity_createTouchPublisher(JNIEnv *env, jobject thiz, jint domain_id,
                                                                   jstring ns) {
    const char *ns_ = env->GetStringUTFChars(ns, nullptr);
    touch_publisher = new TouchPublisher(domain_id, ns_);
    env->ReleaseStringUTFChars(ns, ns_);
}

extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_TouchScreenActivity_destroyTouchPublisher(JNIEnv *env, jobject thiz) {
    delete touch_publisher;
    touch_publisher = nullptr;
}
extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_TouchScreenActivity_publishTouch(JNIEnv *env, jobject thiz, jfloat x, jfloat y, jfloat z) {
    timespec now;
    clock_gettime(CLOCK_REALTIME, &now);

    touch_publisher->point_msg.x() = x;
    touch_publisher->point_msg.y() = y;
    touch_publisher->point_msg.z() = z;

    touch_publisher->publish();
}
extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_TouchScreenActivity_createJoyPublisher(JNIEnv *env, jobject thiz,
                                                                 jint domain_id, jstring ns) {
    const char *ns_ = env->GetStringUTFChars(ns, nullptr);
    joy_publisher2 = new JoyPublisher(domain_id, ns_);
    env->ReleaseStringUTFChars(ns, ns_);
}
extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_TouchScreenActivity_destroyJoyPublisher(JNIEnv *env, jobject thiz) {
    delete joy_publisher2;
    joy_publisher2 = nullptr;
}
extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_TouchScreenActivity_publishJoy(JNIEnv *env, jobject thiz,
                                                         jfloatArray axes, jintArray buttons) {
    timespec now;
    clock_gettime(CLOCK_REALTIME, &now);
    joy_publisher2->joy_msg.header().stamp().sec() = now.tv_sec;
    joy_publisher2->joy_msg.header().stamp().nanosec() = now.tv_nsec;

    joy_publisher2->joy_msg.axes().resize(env->GetArrayLength(axes));
    joy_publisher2->joy_msg.buttons().resize(env->GetArrayLength(buttons));
    env->GetFloatArrayRegion(axes, 0, joy_publisher2->joy_msg.axes().size(),
                             joy_publisher2->joy_msg.axes().data());
    env->GetIntArrayRegion(buttons, 0, joy_publisher2->joy_msg.buttons().size(),
                           joy_publisher2->joy_msg.buttons().data());

    joy_publisher2->publish();
}


extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_ControlPanelActivity_createControlPublisher(JNIEnv *env, jobject thiz,
                                                                      jint domain_id, jstring ns) {
    const char *ns_ = env->GetStringUTFChars(ns, nullptr);
    control_publisher = new ControlPublisher(domain_id, ns_);
    env->ReleaseStringUTFChars(ns, ns_);
}

extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_ControlPanelActivity_destroyControlPublisher(JNIEnv *env, jobject thiz) {
    delete control_publisher;
    control_publisher = nullptr;
}

extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_ControlPanelActivity_publishControl(JNIEnv *env, jobject thiz,
                                                              jfloatArray axes, jintArray buttons) {
    timespec now;
    clock_gettime(CLOCK_REALTIME, &now);
    control_publisher->control_msg.header().stamp().sec() = now.tv_sec;
    control_publisher->control_msg.header().stamp().nanosec() = now.tv_nsec;

    control_publisher->control_msg.axes().resize(env->GetArrayLength(axes));
    control_publisher->control_msg.buttons().resize(env->GetArrayLength(buttons));
    env->GetFloatArrayRegion(axes, 0, control_publisher->control_msg.axes().size(),
                             control_publisher->control_msg.axes().data());
    env->GetIntArrayRegion(buttons, 0, control_publisher->control_msg.buttons().size(),
                           control_publisher->control_msg.buttons().data());

    control_publisher->publish();
}


extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_JoyPublisherService_createJoyPublisher(JNIEnv *env, jobject thiz,
                                                                 jint domain_id, jstring ns) {
    const char *ns_ = env->GetStringUTFChars(ns, nullptr);
    joy_service_publisher = new JoyPublisher(domain_id, ns_);
    env->ReleaseStringUTFChars(ns, ns_);
}
extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_JoyPublisherService_destroyJoyPublisher(JNIEnv *env, jobject thiz) {
    delete joy_service_publisher;
    joy_service_publisher = nullptr;
}
extern "C"
JNIEXPORT void JNICALL
Java_jp_eyrin_rosjoydroid_JoyPublisherService_publishJoy(JNIEnv *env, jobject thiz,
                                                         jfloatArray axes, jintArray buttons) {
    timespec now;
    clock_gettime(CLOCK_REALTIME, &now);
    joy_service_publisher->joy_msg.header().stamp().sec() = now.tv_sec;
    joy_service_publisher->joy_msg.header().stamp().nanosec() = now.tv_nsec;

    joy_service_publisher->joy_msg.axes().resize(env->GetArrayLength(axes));
    joy_service_publisher->joy_msg.buttons().resize(env->GetArrayLength(buttons));
    env->GetFloatArrayRegion(axes, 0, joy_service_publisher->joy_msg.axes().size(),
                             joy_service_publisher->joy_msg.axes().data());
    env->GetIntArrayRegion(buttons, 0, joy_service_publisher->joy_msg.buttons().size(),
                           joy_service_publisher->joy_msg.buttons().data());

    joy_service_publisher->publish();
}